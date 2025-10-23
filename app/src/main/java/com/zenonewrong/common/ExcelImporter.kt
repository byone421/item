package com.zenonewrong.common

import android.content.Context
import android.net.Uri
import android.util.Log
import com.zenonewrong.AppDatabase
import com.zenonewrong.entity.Classify
import com.zenonewrong.entity.ItemInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.core.net.toUri

class ExcelImporter(private val context: Context) {

    suspend fun importFromExcel(filePath: String): String = withContext(Dispatchers.IO) {
        try {
            Log.d("ExcelImporter", "开始导入Excel文件: $filePath")

            val database = AppDatabase.getDatabase(context)
            val uri = filePath.toUri()

            Log.d("ExcelImporter", "解析的URI: $uri")

            // 获取输入流
            val inputStream: InputStream? = when {
                filePath.startsWith("content://") -> {
                    Log.d("ExcelImporter", "使用content resolver打开文件")
                    context.contentResolver.openInputStream(uri)
                }
                filePath.startsWith("/document/") -> {
                    // 处理文档提供者URI，如 /document/primary:baidu/导出_135522.xlsx
                    Log.d("ExcelImporter", "处理文档提供者URI: $filePath")
                    val contentUri = "content://$filePath".toUri()
                    context.contentResolver.openInputStream(contentUri)
                }
                filePath.startsWith("file://") -> {
                    val path = uri.path ?: filePath
                    Log.d("ExcelImporter", "使用file://协议，路径: $path")
                    val file = java.io.File(path)
                    if (file.exists()) {
                        file.inputStream()
                    } else {
                        throw Exception("文件不存在: $path")
                    }
                }
                filePath.contains(":/") && !filePath.startsWith("http") -> {
                    Log.d("ExcelImporter", "使用绝对路径: $filePath")
                    val file = java.io.File(filePath)
                    if (file.exists()) {
                        file.inputStream()
                    } else {
                        throw Exception("文件不存在: $filePath")
                    }
                }
                else -> throw Exception("不支持的文件路径格式: $filePath")
            }

            inputStream?.use { stream ->
                val workbook = XSSFWorkbook(stream)

                // 读取物品信息表（Sheet1）
                val itemSheet = workbook.getSheetAt(0) // 物品信息
                val itemInfoList = mutableListOf<ItemInfo>()

                // 跳过标题行（第0行）和表头行（第1行），从第2行开始读取数据
                for (rowIndex in 2..itemSheet.lastRowNum) {
                    val row = itemSheet.getRow(rowIndex) ?: continue

                    try {
                        val item = ItemInfo(
                            id = 0, // 设置为0，让数据库自动生成ID
                            name = getCellStringValue(row, 1),
                            producedDate = getCellStringValue(row, 2),
                            storageDuration = getCellStringValue(row, 3),
                            storageUnit = getCellStringValue(row, 4),
                            maturityDate = getCellStringValue(row, 5),
                            classifyId = getCellLongValue(row, 6),
                            classifyName = getCellStringValue(row, 7),
                            purchasePrice = getCellStringValue(row, 8),
                            purchaseDate = getCellStringValue(row, 9),
                            storageLocation = getCellStringValue(row, 10),
                            storageQuantity = getCellStringValue(row, 11),
                            remark = getCellStringValue(row, 12)
                        )

                        if (item.name.isNotEmpty()) {
                            itemInfoList.add(item)
                        }
                    } catch (e: Exception) {
                        Log.w("ExcelImporter", "解析第${rowIndex + 1}行数据失败: ${e.message}")
                    }
                }

                // 读取分类信息表（Sheet2）
                val classifySheet = workbook.getSheetAt(1) // 分类信息
                val classifyList = mutableListOf<Classify>()

                // 跳过标题行（第0行）和表头行（第1行），从第2行开始读取数据
                for (rowIndex in 2..classifySheet.lastRowNum) {
                    val row = classifySheet.getRow(rowIndex) ?: continue

                    try {
                        val createTimeStr = getCellStringValue(row, 3)
                        val createTime = try {
                            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                                .parse(createTimeStr)?.time ?: System.currentTimeMillis()
                        } catch (e: Exception) {
                            System.currentTimeMillis()
                        }

                        val classify = Classify(
                            id = getCellLongValue(row, 0),
                            name = getCellStringValue(row, 1),
                            sortOrder = getCellIntValue(row, 2),
                            createTime = createTime,
                            showOnHome = getCellStringValue(row, 4) == "是"
                        )

                        if (classify.name.isNotEmpty()) {
                            classifyList.add(classify)
                        }
                    } catch (e: Exception) {
                        Log.w("ExcelImporter", "解析分类第${rowIndex + 1}行数据失败: ${e.message}")
                    }
                }

                workbook.close()

                Log.d("ExcelImporter", "解析完成：物品数量=${itemInfoList.size}, 分类数量=${classifyList.size}")

                // 清空现有数据
                database.itemInfoDao().deleteAll()
                database.classifyDao().deleteAll()
                Log.d("ExcelImporter", "已清空现有数据")

                // 导入新数据
                var importedClassifies = 0
                var importedItems = 0

                // 先导入分类数据
                classifyList.forEach { classify ->
                    database.classifyDao().insertData(classify)
                    importedClassifies++
                }

                // 再导入物品数据
                itemInfoList.forEach { item ->
                    database.itemInfoDao().insertData(item)
                    importedItems++
                }

                Log.d("ExcelImporter", "导入完成：分类=$importedClassifies, 物品=$importedItems")

                "导入成功！已导入 $importedClassifies 个分类和 $importedItems 个物品"
            } ?: throw Exception("无法打开文件")

        } catch (e: Exception) {
            Log.e("ExcelImporter", "导入失败", e)
            throw Exception("导入失败：${e.message}")
        }
    }

    private fun getCellStringValue(row: org.apache.poi.ss.usermodel.Row, columnIndex: Int): String {
        val cell = row.getCell(columnIndex)
        return when (cell?.cellType) {
            CellType.STRING -> cell.stringCellValue.trim()
            CellType.NUMERIC -> cell.numericCellValue.toString()
            CellType.BOOLEAN -> cell.booleanCellValue.toString()
            CellType.FORMULA -> {
                try {
                    cell.numericCellValue.toString()
                } catch (e: Exception) {
                    cell.stringCellValue.trim()
                }
            }
            else -> ""
        }
    }

    private fun getCellIntValue(row: org.apache.poi.ss.usermodel.Row, columnIndex: Int): Int {
        val cell = row.getCell(columnIndex)
        return when (cell?.cellType) {
            CellType.NUMERIC -> cell.numericCellValue.toInt()
            CellType.STRING -> {
                try {
                    cell.stringCellValue.trim().toInt()
                } catch (e: Exception) {
                    0
                }
            }
            else -> 0
        }
    }

    private fun getCellLongValue(row: org.apache.poi.ss.usermodel.Row, columnIndex: Int): Long {
        val cell = row.getCell(columnIndex)
        return when (cell?.cellType) {
            CellType.NUMERIC -> cell.numericCellValue.toLong()
            CellType.STRING -> {
                try {
                    cell.stringCellValue.trim().toLong()
                } catch (e: Exception) {
                    0L
                }
            }
            else -> 0L
        }
    }
}