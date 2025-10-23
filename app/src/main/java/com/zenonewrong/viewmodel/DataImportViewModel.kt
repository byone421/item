package com.zenonewrong.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zenonewrong.common.DataImporter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.core.net.toUri

class DataImportViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application.applicationContext
    private val dataImporter = DataImporter(context)

    private val _selectedFile = MutableStateFlow<String?>(null)
    val selectedFile: StateFlow<String?> = _selectedFile.asStateFlow()

    private val _isImporting = MutableStateFlow(false)
    val isImporting: StateFlow<Boolean> = _isImporting.asStateFlow()

    private val _showWarningDialog = MutableStateFlow(false)
    val showWarningDialog: StateFlow<Boolean> = _showWarningDialog.asStateFlow()

    private val _importResult = MutableStateFlow<String?>(null)
    val importResult: StateFlow<String?> = _importResult.asStateFlow()

    private val _selectedFileType = MutableStateFlow<Int>(0)

    fun selectFile(fileUri: String) {
        _selectedFile.value = fileUri
        Log.d("DataImportViewModel", "选择的文件URI: $fileUri")
    }

    fun showImportWarning() {
        _showWarningDialog.value = true
    }

    fun hideWarningDialog() {
        _showWarningDialog.value = false
    }

    fun importFromCsv() {
        val file = _selectedFile.value
        if (file == null || !file.endsWith("csv")) {
            _importResult.value = "请先选择要导入的CSV文件"
            return
        }
        _showWarningDialog.value = false
        viewModelScope.launch {
            try {
                _isImporting.value = true
                val result = dataImporter.importFromExcel(file,_selectedFileType.value)
                _importResult.value = result
                Log.d("DataImportViewModel", "导入成功: $result")
            } catch (e: Exception) {
                val errorMessage = "导入失败：${e.message}"
                _importResult.value = errorMessage
                Log.e("DataImportViewModel", "导入失败", e)
            } finally {
                _isImporting.value = false
            }
        }
    }

    fun getFileName(): String? {
        val file = _selectedFile.value ?: return null
        return when {
            file.startsWith("content://") -> {
                // 从content URI中提取文件名
                file.toUri().lastPathSegment?.substringAfterLast("/")
                    ?: file.substringAfterLast("/")
            }
            else -> {
                file.substringAfterLast("/")
            }
        }
    }

    fun setImportResult(string: String) {
        _importResult.value=string
    }

    /**
     * 0物品 1分类
     */
    fun setSelectedFileType(type: Int) {
        _selectedFileType.value=type
    }
}