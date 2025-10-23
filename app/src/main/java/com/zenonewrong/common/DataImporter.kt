package com.zenonewrong.common

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.zenonewrong.AppDatabase
import com.zenonewrong.entity.Classify
import com.zenonewrong.entity.ItemInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.core.net.toUri
import com.opencsv.CSVReader
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader

class DataImporter(private val context: Context) {

    fun importCsvToClassify(filePath: String): List<Classify> {
        val reader = CSVReader(FileReader(filePath))
        val allLines = reader.readAll()
        reader.close()

        // 跳过表头
        return allLines.drop(1).map { row ->
            Classify(
                id = row[0].toLongOrNull() ?: 0L,
                name = row[1],
                sortOrder = row[2].toIntOrNull() ?: 0,
                createTime = row[3].toLongOrNull() ?: System.currentTimeMillis(),
                showOnHome = when {
                    row.size > 4 -> row[4].equals("true", ignoreCase = true)
                    else -> true
                }
            )
        }
    }

    fun importCsvToItemInfo(filePath: String): List<ItemInfo> {
        val reader = CSVReader(FileReader(filePath))
        val allLines = reader.readAll()
        reader.close()

        // 跳过第一行（表头）
        return allLines.drop(1).map { row ->
            ItemInfo(
                id = row[0].toLongOrNull() ?: 0L,
                name = row[1],
                producedDate = row[2],
                storageDuration = row[3],
                storageUnit = row[4],
                maturityDate = row[5],
                classifyId = row[6].toLongOrNull(),
                classifyName = row[7],
                purchasePrice = row[8],
                purchaseDate = row[9],
                storageLocation = row[10],
                storageQuantity = row[11],
                remark = row.getOrNull(12) ?: ""
            )
        }
    }
    suspend fun importFromExcel(filePath: String,selectedFileType: Int): String = withContext(Dispatchers.IO) {
        try {

            val database = AppDatabase.getDatabase(context)
            val resolveFilePath = resolveFilePath(context, filePath)
            var importedItems = 0
            //导入物品

            if(selectedFileType==0){
                val itemInfoList = importCsvToItemInfo(resolveFilePath)
                database.itemInfoDao().deleteAll()
                itemInfoList.forEach { item ->
                    database.itemInfoDao().insertData(item)
                    importedItems++
                }
            }
            //导入分类
            else{
                val classifyList = importCsvToClassify(resolveFilePath)
                database.classifyDao().deleteAll()
                classifyList.forEach { item ->
                    database.classifyDao().insertData(item)
                    importedItems++
                }
            }
            "导入成功！已导入 $importedItems 条数据"
        } catch (e: Exception) {
            throw Exception("导入失败：${e.message}")
        }
    }


    fun resolveFilePath(context: Context, filePath: String): String {
        val uri = filePath.toUri()
        return when {
            filePath.startsWith("content://") -> {
                // 临时复制到缓存目录，生成真实文件路径
                val fileName = getFileNameFromUri(context, uri) ?: "import.csv"
                val tempFile = File(context.cacheDir, fileName)

                context.contentResolver.openInputStream(uri)?.use { input ->
                    FileOutputStream(tempFile).use { output ->
                        input.copyTo(output)
                    }
                }
                tempFile.absolutePath
            }

            filePath.startsWith("file://") -> {
                uri.path ?: throw Exception("无法解析 file:// 路径")
            }

            filePath.startsWith("/") -> {
                val file = File(filePath)
                if (file.exists()) file.absolutePath else throw Exception("文件不存在: $filePath")
            }

            else -> throw Exception("不支持的文件路径格式: $filePath")
        }
    }


    fun getFileNameFromUri(context: Context, uri: Uri): String? {
        var name: String? = null
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && nameIndex >= 0) {
                name = cursor.getString(nameIndex)
            }
        }
        return name
    }


}