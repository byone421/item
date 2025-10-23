package com.zenonewrong.common

import android.content.Context
import android.os.Environment
import android.util.Log
import com.opencsv.CSVWriter
import com.zenonewrong.AppDatabase
import com.zenonewrong.entity.Classify
import com.zenonewrong.entity.ItemInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class DataExporter(private val context: Context) {

    val TAG: String ="DataExporter"
    
    /**
     * 尝试创建目录，带异常处理
     */
    private fun tryCreateDirectory(dir: File): Boolean {
        return try {
            if (!dir.exists()) {
                val created = dir.mkdirs()
                Log.d(TAG, "尝试创建目录: ${dir.absolutePath}, 结果: $created")
                if (!created) {
                    // 检查是否是因为父目录不存在
                    val parent = dir.parentFile
                    if (parent != null && !parent.exists()) {
                        Log.w(TAG, "父目录不存在: ${parent.absolutePath}")
                        return false
                    }
                    // 检查权限
                    if (!dir.canWrite()) {
                        Log.w(TAG, "没有写入权限: ${dir.absolutePath}")
                        return false
                    }
                }
                created
            } else {
                Log.d(TAG, "目录已存在: ${dir.absolutePath}")
                true
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "安全异常，无法创建目录: ${dir.absolutePath}", e)
            false
        } catch (e: Exception) {
            Log.e(TAG, "创建目录失败: ${dir.absolutePath}", e)
            false
        }
    }

    fun List<ItemInfo>.itemInfoToCsvLines(): List<Array<String>> {
        val header = arrayOf(
            "ID", "名称", "生产日期", "保质期", "保质单位",
            "到期日期", "分类ID", "分类名称", "价格", "购买日期",
            "存放位置", "存放数量", "备注"
        )

        val data = this.map { item ->
            arrayOf(
                item.id.toString(),
                item.name,
                item.producedDate,
                item.storageDuration,
                item.storageUnit,
                item.maturityDate,
                item.classifyId?.toString() ?: "",
                item.classifyName,
                item.purchasePrice,
                item.purchaseDate,
                item.storageLocation,
                item.storageQuantity,
                item.remark
            )
        }

        return listOf(header) + data
    }

    fun List<Classify>.classifyToCsvLines(): List<Array<String>> {
        val header = arrayOf("ID", "名称", "排序", "创建时间", "首页展示")
        val data = this.map { item ->
            arrayOf(
                item.id.toString(),
                item.name,
                item.sortOrder.toString(),
                item.createTime.toString(),
                item.showOnHome.toString()
            )
        }
        return listOf(header) + data
    }
    suspend fun exportToCsv(selectedFileType: Int): String = withContext(Dispatchers.IO) {
        try {
            val database = AppDatabase.getDatabase(context)
            var baseDir: String? = null
            var itemDir: File? = null

            // 尝试使用外部存储的Download目录（用户可访问）
            val downloadDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "csv")
            if (tryCreateDirectory(downloadDir)) {
                baseDir = downloadDir.absolutePath
                itemDir = downloadDir
                Log.d(TAG, "使用Download目录: $baseDir")
            } else {
                // 退回到应用专属目录
                val appExternalDir = context.getExternalFilesDir("excel")
                if (appExternalDir != null && tryCreateDirectory(appExternalDir)) {
                    baseDir = appExternalDir.absolutePath
                    itemDir = appExternalDir
                    Log.d(TAG, "使用应用专属目录: $baseDir")
                } else {
                    // 最后退回到内部存储
                    val internalDir = File(context.filesDir, "excel")
                    if (tryCreateDirectory(internalDir)) {
                        baseDir = internalDir.absolutePath
                        itemDir = internalDir
                        Log.d(TAG, "使用内部存储目录: $baseDir")
                    } else {
                        throw Exception("无法创建导出目录")
                    }
                }
            }
            var  file: File
            if(selectedFileType == 0){
                val itemInfoList = database.itemInfoDao().getAllItems()
                val fileName = "导出物品_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.csv"
                file = File(itemDir, fileName)
                try {
                    val writer = CSVWriter(FileWriter(file))
                    writer.writeAll(itemInfoList.itemInfoToCsvLines())
                    writer.close()
                    // 提示用户文件已保存
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }else{
                val classifyList = database.classifyDao().getAllClassifiesData()
                val fileName = "导出分类_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.csv"
                file = File(itemDir, fileName)
                try {
                    val writer = CSVWriter(FileWriter(file))
                    writer.writeAll(classifyList.classifyToCsvLines())
                    writer.close()
                    // 提示用户文件已保存
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            file.absolutePath
        } catch (e: Exception) {
            Log.e(TAG, "导出失败", e)
            throw e
        }
    }

}