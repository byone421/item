package com.zenonewrong.viewmodel

import android.app.Application
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zenonewrong.AppDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.zenonewrong.bean.ClassifyState
import com.zenonewrong.entity.Classify
import com.zenonewrong.entity.ItemInfo
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn

class ClassifyViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val classifyDao = database.classifyDao()

    // 初始化输入状态
    private val _classifyState = MutableStateFlow(ClassifyState())
    val classifyState: StateFlow<ClassifyState> = _classifyState.asStateFlow()

    val allClassifies =
        classifyDao.getAllClassifies().stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // 消息事件类型
    sealed class MessageEvent {
        data class ShowSnackbar(val message: String) : MessageEvent()
        data class Success(val message: String) : MessageEvent()
    }

    private val _messageEvent = MutableSharedFlow<MessageEvent>()
    val messageEvent = _messageEvent.asSharedFlow()


    // 打开/关闭对话框
    fun toggleDialog(show: Boolean) {
        _classifyState.update { it.copy(isDialogVisible = show) }
    }

    fun cleanState() {
        _classifyState.update {
            it.copy(
                id = null,
                name = "",
                sortOrder = "0",
                showOnHome = false,
                editingClassify = false
            )
        }
    }


    // 更新输入字段
    fun updateName(name: String) {
        _classifyState.update { it.copy(name = name) }
    }

    fun updateSortOrder(sortOrder: String) {
        // 验证输入是否为数字
        if (sortOrder.all { it.isDigit() }) {
            _classifyState.update { it.copy(sortOrder = sortOrder) }
        }
    }

    fun updateShowOnHome(showOnHome: Boolean) {
        _classifyState.update { it.copy(showOnHome = showOnHome) }
    }

    // 添加新分类
    fun addClassify() {
        viewModelScope.launch {
            try {
                val id = _classifyState.value.id ?: 0
                //查询该分类是否存在
                if (id == 0L) {
                    val count = classifyDao.getCountByClassifyName(_classifyState.value.name)
                    if (count > 0) {
                        _messageEvent.emit(MessageEvent.ShowSnackbar("该分类已经存在"))
                        return@launch
                    }
                }
                val sortOrder = _classifyState.value.sortOrder.toIntOrNull() ?: 0

                val classify = Classify(
                    id = id,
                    name = _classifyState.value.name,
                    sortOrder = sortOrder,
                    createTime = System.currentTimeMillis(),
                    showOnHome = _classifyState.value.showOnHome
                )
                classifyDao.insertData(classify)
                _messageEvent.emit(MessageEvent.ShowSnackbar("操作成功"))
                cleanState()
            } catch (e: Exception) {
                // 失败处理
                _messageEvent.emit(
                    MessageEvent.ShowSnackbar("添加失败: ${e.message ?: "未知错误"}")
                )
            }
        }
    }

    //编辑
    fun startEdit(classify: Classify) {
        _classifyState.update {
            it.copy(
                id = classify.id,
                name = classify.name,
                sortOrder = classify.sortOrder.toString(),
                showOnHome = classify.showOnHome,
                editingClassify = true
            )
        }
        toggleDialog(true)
    }

    // 删除分类
    fun deleteClassify(classify: Classify) {
        viewModelScope.launch {
            classifyDao.deleteData(classify)
        }
    }
}