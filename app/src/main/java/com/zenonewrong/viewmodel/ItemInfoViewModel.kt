package com.zenonewrong.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zenonewrong.AppDatabase
import com.zenonewrong.bean.ItemFormState
import com.zenonewrong.bean.ItemFormState.DateFieldType
import com.zenonewrong.entity.Classify
import com.zenonewrong.entity.ItemInfo
import com.zenonewrong.viewmodel.ClassifyViewModel.MessageEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


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

    // 打开日期选择器
    fun showDatePicker(field: ItemFormState.DateFieldType) {
        _itemFormState.update { it.copy(showDatePicker = true, currentDateField = field) }
    }

    // 关闭日期选择器
    fun dismissDatePicker() {
        _itemFormState.update { it.copy(showDatePicker = false) }
    }

    fun onDateSelected(dateString: String) {
        val currentField = _itemFormState.value.currentDateField
        _itemFormState.update { currentState ->
            when(currentField) {
                DateFieldType.PRODUCED_DATE -> {
                    currentState.copy(producedDate = dateString)
                }
                DateFieldType.MATURITY_DATE -> currentState.copy(maturityDate = dateString)
                DateFieldType.PURCHASE_DATE -> currentState.copy(purchaseDate = dateString)
                null -> currentState
            }
        }
        dismissDatePicker()
    }


    // 打开日期选择器
    fun showStorageUnit() {
        _itemFormState.update { it.copy(showStorageUnit = true) }
    }

    // 关闭日期选择器
    fun dismissStorageUnit() {
        _itemFormState.update { it.copy(showStorageUnit = false) }
    }

    fun onStorageSelect(unit: String) {
        _itemFormState.update { it.copy(showStorageUnit = false, storageUnit = unit).calculateMaturityDate() }
    }

    // 加载现有项目
    fun loadItem(item: ItemInfo) {
        _itemFormState.update { ItemFormState.fromEntity(item) }
    }

    // 根据ID加载项目
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

    // 保存项目
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

    // 清空表单
    fun clearForm() {
        _itemFormState.update { ItemFormState() }
    }

    // 复制时清空id
    fun clearIdForCopy() {
        _itemFormState.update { it.copy(id = 0L) }
    }

    // 扩展函数检查表单有效性
    private fun ItemFormState.isFormValid(): Boolean {
        return isNameValid  &&  isMaturityDateValid
    }
}