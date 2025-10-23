package com.zenonewrong.common

import android.content.Context
import android.media.MediaScannerConnection
import android.os.Environment
import android.util.Log
import com.zenonewrong.AppDatabase
import com.zenonewrong.entity.Classify
import com.zenonewrong.entity.ItemInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class ExcelExporter(private val context: Context) {

    /**
     * 尝试创建目录，带异常处理
     */
    private fun tryCreateDirectory(dir: File): Boolean {
        return try {
            if (!dir.exists()) {
                val created = dir.mkdirs()
                Log.d("ExcelExporter", "尝试创建目录: ${dir.absolutePath}, 结果: $created")
                if (!created) {
                    // 检查是否是因为父目录不存在
                    val parent = dir.parentFile
                    if (parent != null && !parent.exists()) {
                        Log.w("ExcelExporter", "父目录不存在: ${parent.absolutePath}")
                        return false
                    }
                    // 检查权限
                    if (!dir.canWrite()) {
                        Log.w("ExcelExporter", "没有写入权限: ${dir.absolutePath}")
                        return false
                    }
                }
                created
            } else {
                Log.d("ExcelExporter", "目录已存在: ${dir.absolutePath}")
                true
            }
        } catch (e: SecurityException) {
            Log.e("ExcelExporter", "安全异常，无法创建目录: ${dir.absolutePath}", e)
            false
        } catch (e: Exception) {
            Log.e("ExcelExporter", "创建目录失败: ${dir.absolutePath}", e)
            false
        }
    }

    suspend fun exportToExcel(): String = withContext(Dispatchers.IO) {
        var workbook: XSSFWorkbook? = null
        try {
            Log.d("ExcelExporter", "开始导出Excel")

            val database = AppDatabase.getDatabase(context)
            val itemInfoList = database.itemInfoDao().getAllItems()
            val classifyList = database.classifyDao().getAllClassifiesExcel()

            Log.d("ExcelExporter", "获取到数据：物品数量=${itemInfoList.size}, 分类数量=${classifyList.size}")

            workbook = XSSFWorkbook()

            // 创建样式
            val headerStyle = workbook.createCellStyle()
            headerStyle.fillForegroundColor = IndexedColors.GREY_25_PERCENT.index
            headerStyle.fillPattern = FillPatternType.SOLID_FOREGROUND

            // Sheet1: ItemInfo
            val itemSheet = workbook.createSheet("物品信息")
            createItemInfoSheet(itemSheet, itemInfoList, headerStyle)

            // Sheet2: Classify
            val classifySheet = workbook.createSheet("分类信息")
            createClassifySheet(classifySheet, classifyList, headerStyle)

            var baseDir: String? = null
            var itemDir: File? = null

            // 尝试使用外部存储的Download目录（用户可访问）
            val downloadDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "excel")
            if (tryCreateDirectory(downloadDir)) {
                baseDir = downloadDir.absolutePath
                itemDir = downloadDir
                Log.d("ExcelExporter", "使用Download目录: $baseDir")
            } else {
                // 退回到应用专属目录
                val appExternalDir = context.getExternalFilesDir("excel")
                if (appExternalDir != null && tryCreateDirectory(appExternalDir)) {
                    baseDir = appExternalDir.absolutePath
                    itemDir = appExternalDir
                    Log.d("ExcelExporter", "使用应用专属目录: $baseDir")
                } else {
                    // 最后退回到内部存储
                    val internalDir = File(context.filesDir, "excel")
                    if (tryCreateDirectory(internalDir)) {
                        baseDir = internalDir.absolutePath
                        itemDir = internalDir
                        Log.d("ExcelExporter", "使用内部存储目录: $baseDir")
                    } else {
                        throw Exception("无法创建导出目录")
                    }
                }
            }

            Log.d("ExcelExporter", "最终使用的目录: ${itemDir!!.absolutePath}")
            Log.d("ExcelExporter", "目录是否存在: ${itemDir.exists()}")

            val fileName = "导出_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.xlsx"
            val file = File(itemDir, fileName)

            Log.d("ExcelExporter", "准备保存文件: ${file.absolutePath}")

            FileOutputStream(file).use { fileOut ->
                workbook.write(fileOut)
            }

            Log.d("ExcelExporter", "文件保存成功: ${file.absolutePath}")
            Log.d("ExcelExporter", "文件是否存在: ${file.exists()}")
            Log.d("ExcelExporter", "文件大小: ${file.length()} bytes")

            // 扫描文件使其在文件管理器中可见
            try {
                MediaScannerConnection.scanFile(
                    context,
                    arrayOf(file.absolutePath),
                    arrayOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                ) { path, uri ->
                    Log.d("ExcelExporter", "文件扫描完成: path=$path, uri=$uri")
                }
            } catch (e: Exception) {
                Log.w("ExcelExporter", "文件扫描失败: ${e.message}")
            }

            file.absolutePath
        } catch (e: Exception) {
            Log.e("ExcelExporter", "导出失败", e)
            throw e
        } finally {
            workbook?.close()
        }
    }

    private fun createItemInfoSheet(sheet: org.apache.poi.ss.usermodel.Sheet, items: List<ItemInfo>, headerStyle: CellStyle) {
        // 创建标题行
        val titleRow = sheet.createRow(0)
        val titleCell = titleRow.createCell(0)
        titleCell.setCellValue("物品信息表")
        titleCell.cellStyle = headerStyle
        sheet.addMergedRegion(CellRangeAddress(0, 0, 0, 11))

        // 创建表头
        val headerRow = sheet.createRow(1)
        val headers = ItemInfo.getTableColumns()

        headers.forEachIndexed { index, header ->
            val cell = headerRow.createCell(index)
            cell.setCellValue(header)
            cell.cellStyle = headerStyle
        }

        // 填充数据
        items.forEachIndexed { itemIndex, item ->
            val row = sheet.createRow(itemIndex + 2)
            row.createCell(0).setCellValue(item.id.toString())
            row.createCell(1).setCellValue(item.name)
            row.createCell(2).setCellValue(item.producedDate)
            row.createCell(3).setCellValue(item.storageDuration)
            row.createCell(4).setCellValue(item.storageUnit)
            row.createCell(5).setCellValue(item.maturityDate)
            row.createCell(6).setCellValue(item.classifyId.toString())
            row.createCell(7).setCellValue(item.classifyName)
            row.createCell(8).setCellValue(item.purchasePrice)
            row.createCell(9).setCellValue(item.purchaseDate)
            row.createCell(10).setCellValue(item.storageLocation)
            row.createCell(11).setCellValue(item.storageQuantity)
            row.createCell(12).setCellValue(item.remark)
        }

        val columnWidths = intArrayOf(3000, 4000, 3000, 2000, 2000, 3000, 3000, 3000, 3000, 3000, 3000, 3000, 4000)
        for (i in columnWidths.indices) {
            if (i < 13) { // 确保不超出列数
                sheet.setColumnWidth(i, columnWidths[i])
            }
        }
    }

    private fun createClassifySheet(sheet: org.apache.poi.ss.usermodel.Sheet, classifies: List<Classify>, headerStyle: CellStyle) {
        // 创建标题行
        val titleRow = sheet.createRow(0)
        val titleCell = titleRow.createCell(0)
        titleCell.setCellValue("分类信息表")
        titleCell.cellStyle = headerStyle
        sheet.addMergedRegion(CellRangeAddress(0, 0, 0, 4))

        // 创建表头
        val headerRow = sheet.createRow(1)
        val headers = Classify.getTableColumns()

        headers.forEachIndexed { index, header ->
            val cell = headerRow.createCell(index)
            cell.setCellValue(header)
            cell.cellStyle = headerStyle
        }

        // 填充数据
        classifies.forEachIndexed { classifyIndex, classify ->
            val row = sheet.createRow(classifyIndex + 2)
            row.createCell(0).setCellValue(classify.id.toString())
            row.createCell(1).setCellValue(classify.name)
            row.createCell(2).setCellValue(classify.sortOrder.toDouble())

            // 格式化创建时间
            val createTimeStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(Date(classify.createTime))
            row.createCell(3).setCellValue(createTimeStr)

            row.createCell(4).setCellValue(if (classify.showOnHome) "是" else "否")
        }

        // 手动设置列宽
        val columnWidths = intArrayOf(3000, 4000, 2000, 5000, 4000)
        for (i in columnWidths.indices) {
            if (i < 5) { // 确保不超出列数
                sheet.setColumnWidth(i, columnWidths[i])
            }
        }
    }
}