package com.zenonewrong.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zenonewrong.common.DataExporter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DataStoreViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application.applicationContext
    private val dataExporter = DataExporter(context)

    private val _isExporting = MutableStateFlow(false)
    val isExporting: StateFlow<Boolean> = _isExporting.asStateFlow()

    private val _exportResult = MutableStateFlow<String?>(null)
    val exportResult: StateFlow<String?> = _exportResult.asStateFlow()

    private val _selectedFileType = MutableStateFlow<Int>(0)



    fun exportToCsv() {
        viewModelScope.launch {
            try {
                _isExporting.value = true
                val result = dataExporter.exportToCsv(_selectedFileType.value)
                _exportResult.value = "导出成功！文件路径：$result"
            } catch (e: Exception) {
                _exportResult.value = "导出失败：${e.message}"
                Log.e("DataStoreViewModel", "导出失败", e)
            } finally {
                _isExporting.value = false
            }
        }
    }


    fun getExportDirectoryHint(): String {
        return "文件将导出到文件管理器根目录下的Download/csv目录或应用专属目录中"
    }

    /**
     * 0物品 1分类
     */
    fun setSelectedFileType(type: Int) {
        _selectedFileType.value=type
    }
}