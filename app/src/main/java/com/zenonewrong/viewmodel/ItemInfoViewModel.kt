package com.zenonewrong.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zenonewrong.AppDatabase
import com.zenonewrong.bean.ItemFormState
import com.zenonewrong.bean.ItemFormState.DateFieldType
import com.zenonewrong.entity.Classify
import com.zenonewrong.entity.ItemInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class ItemInfoViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val itemInfoDao = database.itemInfoDao()

    private val _itemFormState = MutableStateFlow(ItemFormState())
    val formState: StateFlow<ItemFormState> = _itemFormState.asStateFlow()

    private val _messageEvent = MutableSharedFlow<MessageEvent>()
    val messageEvent = _messageEvent.asSharedFlow()

    sealed class MessageEvent {
        data class ShowSnackbar(val message: String) : MessageEvent()
        data class Success(val message: String) : MessageEvent()
    }

    fun updateName(name: String) {
        _itemFormState.update { it.copy(name = name) }
    }

    fun updateProducedDate(date: String) {
        _itemFormState.update {
            it.copy(
                producedDate = date,
                showDatePicker = false
            ).calculateMaturityDate()
        }
    }

    fun updateClassify(classify: Classify) {
        Log.d("ItemInfoViewModel", "Updating classify: ${classify.name}, ID: ${classify.id}")
        _itemFormState.update {
            it.copy(
                classifyName = classify.name,
                classifyId = classify.id
            )
        }
        Log.d("ItemInfoViewModel", "Updated formState classifyName: ${_itemFormState.value.classifyName}")
    }

    fun updateStorageDuration(duration: String) {
        _itemFormState.update {
            it.copy(storageDuration = duration).calculateMaturityDate()
        }
    }

    fun updateStorageUnit(unit: String) {
        _itemFormState.update {
            it.copy(storageUnit = unit).calculateMaturityDate()
        }
    }

    fun updatePurchaseDate(date: String) {
        _itemFormState.update {
            it.copy(
                purchaseDate = date,
                showDatePicker = false
            )
        }
    }

    fun updatePurchasePrice(price: String) {
        _itemFormState.update {
            it.copy(
                purchasePrice = price,
            )
        }
    }

    fun updateStorageLocation(location: String) {
        _itemFormState.update { it.copy(storageLocation = location) }
    }

    fun updateStorageQuantity(quantity: String) {
        _itemFormState.update { it.copy(storageQuantity = quantity) }
    }

    fun updateRemark(remark: String) {
        _itemFormState.update { it.copy(remark = remark) }
    }

    fun addImage(uri: Uri) {
        if (_itemFormState.value.imagePaths.size >= 5) {
            viewModelScope.launch {
                _messageEvent.emit(MessageEvent.ShowSnackbar("最多只能添加5张图片"))
            }
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val savedPath = copyImageToAppStorage(uri)
                _itemFormState.update { state ->
                    state.copy(imagePaths = (state.imagePaths + savedPath).take(5))
                }
            } catch (e: Exception) {
                _messageEvent.emit(MessageEvent.ShowSnackbar("图片保存失败: ${e.message}"))
            }
        }
    }

    fun addImages(uris: List<Uri>) {
        uris.take(5 - _itemFormState.value.imagePaths.size).forEach { addImage(it) }
    }

    fun removeImage(path: String) {
        _itemFormState.update { state ->
            state.copy(imagePaths = state.imagePaths.filterNot { it == path })
        }
        runCatching { File(path).delete() }
    }

    fun showDatePicker(field: ItemFormState.DateFieldType) {
        _itemFormState.update { it.copy(showDatePicker = true, currentDateField = field) }
    }

    fun dismissDatePicker() {
        _itemFormState.update { it.copy(showDatePicker = false) }
    }

    fun onDateSelected(dateString: String) {
        val currentField = _itemFormState.value.currentDateField
        _itemFormState.update { currentState ->
            when (currentField) {
                DateFieldType.PRODUCED_DATE -> currentState.copy(producedDate = dateString)
                DateFieldType.MATURITY_DATE -> currentState.copy(maturityDate = dateString)
                DateFieldType.PURCHASE_DATE -> currentState.copy(purchaseDate = dateString)
                null -> currentState
            }
        }
        dismissDatePicker()
    }

    fun showStorageUnit() {
        _itemFormState.update { it.copy(showStorageUnit = true) }
    }

    fun dismissStorageUnit() {
        _itemFormState.update { it.copy(showStorageUnit = false) }
    }

    fun onStorageSelect(unit: String) {
        _itemFormState.update { it.copy(showStorageUnit = false, storageUnit = unit).calculateMaturityDate() }
    }

    fun loadItem(item: ItemInfo) {
        _itemFormState.update { ItemFormState.fromEntity(item) }
    }

    fun loadItemById(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val item: ItemInfo? = itemInfoDao.findById(id)
                item?.let {
                    _itemFormState.update { ItemFormState.fromEntity(item) }
                }
            } catch (e: Exception) {
                _messageEvent.emit(MessageEvent.ShowSnackbar("加载失败: ${e.message}"))
            }
        }
    }

    fun saveItem() {
        val validatedState = _itemFormState.value.validate()
        _itemFormState.update { validatedState }

        if (!validatedState.isFormValid()) {
            viewModelScope.launch {
                _messageEvent.emit(MessageEvent.ShowSnackbar("必填项缺失或错误"))
            }
            return
        }

        viewModelScope.launch {
            try {
                val item = validatedState.toEntity()
                itemInfoDao.insertData(item)

                _messageEvent.emit(MessageEvent.Success("成功"))
            } catch (e: Exception) {
                _messageEvent.emit(MessageEvent.ShowSnackbar("保存失败: ${e.message}"))
            }
        }
    }

    fun clearForm() {
        _itemFormState.update { ItemFormState() }
    }

    fun clearIdForCopy() {
        _itemFormState.update { it.copy(id = 0L) }
    }

    private fun copyImageToAppStorage(uri: Uri): String {
        val dir = File(getApplication<Application>().filesDir, "item/images")
        if (!dir.exists()) dir.mkdirs()
        val file = File(dir, "${System.currentTimeMillis()}_${uri.lastPathSegment?.hashCode() ?: 0}.jpg")

        getApplication<Application>().contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        } ?: throw IllegalArgumentException("无法读取图片")

        return file.absolutePath
    }

    private fun ItemFormState.isFormValid(): Boolean {
        return isNameValid && isMaturityDateValid
    }
}
