package com.zenonewrong.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zenonewrong.common.DataImporter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.core.net.toUri
import java.io.File
import java.io.FileOutputStream

class DataImportViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application.applicationContext
    private val dataImporter = DataImporter(context)

    private val _selectedFile = MutableStateFlow<String?>(null)
    val selectedFile: StateFlow<String?> = _selectedFile.asStateFlow()

    private val _selectUri = MutableStateFlow<Uri?>(null)
    val selectUri: StateFlow<Uri?> = _selectUri.asStateFlow()

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

//    fun importFromCsv() {
//        val file = _selectedFile.value
//        if (file == null || !file.endsWith("csv")) {
//            _importResult.value = "请先选择要导入的CSV文件"
//            return
//        }
//        _showWarningDialog.value = false
//        viewModelScope.launch {
//            try {
//                _isImporting.value = true
//                val result = dataImporter.importFromExcel(file,_selectedFileType.value)
//                _importResult.value = result
//                Log.d("DataImportViewModel", "导入成功: $result")
//            } catch (e: Exception) {
//                val errorMessage = "导入失败：${e.message}"
//                _importResult.value = errorMessage
//                Log.e("DataImportViewModel", "导入失败", e)
//            } finally {
//                _isImporting.value = false
//            }
//        }
//    }


    fun importCsvDataFormUri(){
        val fileUri = _selectUri.value
        if (fileUri == null) {
            _importResult.value = "请先选择要导入的CSV文件"
            return
        }
        val fileContent = readFileContentFromUri(_selectUri.value!!)
        _showWarningDialog.value = false
        var result = ""
        viewModelScope.launch {
            try {
                _isImporting.value = true
                fileContent?.let { content ->
                    result =  dataImporter.importFromCsvContent(content,_selectedFileType.value)
                }
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
    // 从URI读取文件内容
      fun  readFileContentFromUri(uri: Uri): String? {
          return  dataImporter.readFileContentFromUri(uri)
    }

    fun importFromCsvContent(content: String) {


    }

    fun selectFileUri(uri: Uri) {
        _selectUri.value = uri
    }

    fun restoreImagesFromFolder(folderUri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isImporting.value = true
                val count = copyImagesFromFolder(folderUri)
                _importResult.value = "恢复图片成功：$count 张"
            } catch (e: Exception) {
                _importResult.value = "恢复图片失败：${e.message}"
                Log.e("DataImportViewModel", "恢复图片失败", e)
            } finally {
                _isImporting.value = false
            }
        }
    }

    private fun copyImagesFromFolder(folderUri: Uri): Int {
        val resolver = context.contentResolver
        val treeDocumentId = DocumentsContract.getTreeDocumentId(folderUri)
        val childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(folderUri, treeDocumentId)
        val imageDir = File(context.filesDir, "item/images")
        if (!imageDir.exists()) imageDir.mkdirs()

        var count = 0
        resolver.query(
            childrenUri,
            arrayOf(
                DocumentsContract.Document.COLUMN_DOCUMENT_ID,
                DocumentsContract.Document.COLUMN_DISPLAY_NAME,
                DocumentsContract.Document.COLUMN_MIME_TYPE
            ),
            null,
            null,
            null
        )?.use { cursor ->
            val idIndex = cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DOCUMENT_ID)
            val nameIndex = cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DISPLAY_NAME)
            val mimeIndex = cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_MIME_TYPE)

            while (cursor.moveToNext()) {
                val mimeType = cursor.getString(mimeIndex)
                if (mimeType == DocumentsContract.Document.MIME_TYPE_DIR) continue

                val documentId = cursor.getString(idIndex)
                val displayName = cursor.getString(nameIndex) ?: continue
                val fileUri = DocumentsContract.buildDocumentUriUsingTree(folderUri, documentId)
                val target = File(imageDir, displayName)

                resolver.openInputStream(fileUri)?.use { input ->
                    FileOutputStream(target).use { output ->
                        input.copyTo(output)
                    }
                }
                count++
            }
        }

        return count
    }
}
