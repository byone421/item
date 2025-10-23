package com.zenonewrong.viewmodel

import android.app.Application
import android.content.Intent
import android.os.Build
import android.provider.DocumentsContract
import android.util.Log
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zenonewrong.common.ExcelExporter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class DataStoreViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application.applicationContext
    private val excelExporter = ExcelExporter(context)

    private val _isExporting = MutableStateFlow(false)
    val isExporting: StateFlow<Boolean> = _isExporting.asStateFlow()

    private val _exportResult = MutableStateFlow<String?>(null)
    val exportResult: StateFlow<String?> = _exportResult.asStateFlow()

//    private val _openDirectoryIntent = MutableStateFlow<Intent?>(null)
//    val openDirectoryIntent: StateFlow<Intent?> = _openDirectoryIntent.asStateFlow()

    fun exportToExcel() {
        viewModelScope.launch {
            try {
                _isExporting.value = true
                val result = excelExporter.exportToExcel()
                _exportResult.value = "导出成功！文件路径：$result"
            } catch (e: Exception) {
                _exportResult.value = "导出失败：${e.message}"
                Log.e("DataStoreViewModel", "导出Excel失败", e)
            } finally {
                _isExporting.value = false
            }
        }
    }

    fun exportToExcelTest() {
        viewModelScope.launch {
            try {
                _isExporting.value = true
                val result = excelExporter.exportToExcel()
                _exportResult.value = "导出成功！文件路径：$result"
            } catch (e: Exception) {
                _exportResult.value = "导出失败：${e.message}"
                Log.e("DataStoreViewModel", "导出Excel失败", e)
            } finally {
                _isExporting.value = false
            }
        }
    }

    fun clearExportResult() {
        _exportResult.value = null
    }

    fun getExportDirectoryHint(): String {
        return "文件将导出到文件管理器根目录下的Download/excel目录或应用专属目录中"
    }

//    fun clearOpenDirectoryIntent() {
//        _openDirectoryIntent.value = null
//    }
}