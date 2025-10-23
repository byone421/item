package com.zenonewrong.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zenonewrong.AppDatabase
import com.zenonewrong.entity.ExpiryReminder
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

data class ExpiryReminderConfig(
    val id: Long = 0,
    val days: Int,
    val tag: String,
    val createTime: Long = System.currentTimeMillis()
)

class ExpiryDaysViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val _expiryConfigs = MutableStateFlow<List<ExpiryReminderConfig>>(emptyList())
    val expiryConfigs: StateFlow<List<ExpiryReminderConfig>> = _expiryConfigs

    // 存储数据库中的原始数据，用于同步
    private val _originalConfigs = MutableStateFlow<List<ExpiryReminderConfig>>(emptyList())



    init {
        loadExpiryReminders()
    }

    private val _showDialog = MutableStateFlow(false)
    val showDialog: StateFlow<Boolean> = _showDialog

    private val _editingIndex = MutableStateFlow(-1)
    val editingIndex: StateFlow<Int> = _editingIndex

    private val _tempDays = MutableStateFlow("3")
    val tempDays: StateFlow<String> = _tempDays

    fun showEditDialog(index: Int) {
        _editingIndex.value = index
        _tempDays.value = _expiryConfigs.value[index].days.toString()
        _showDialog.value = true
    }

    fun hideDialog() {
        _showDialog.value = false
        _editingIndex.value = -1
    }

    fun updateTempDays(days: String) {
        _tempDays.value = days
    }

    private fun loadExpiryReminders() {
        viewModelScope.launch {
            database.expiryReminderDao().getAllExpiryReminders().collect { reminders ->
                val configs = reminders.map { reminder ->
                    ExpiryReminderConfig(
                        id = reminder.id,
                        days = reminder.days,
                        tag = reminder.tag,
                        createTime = reminder.createTime
                    )
                }
                _expiryConfigs.value = configs
                _originalConfigs.value = configs
            }
        }
    }

    fun saveConfig() {
        val index = _editingIndex.value
        if (index >= 0 && index < _expiryConfigs.value.size) {
            val config = _expiryConfigs.value[index]
            val newDays = _tempDays.value.toIntOrNull() ?: 3

            // 直接保存到数据库
            viewModelScope.launch {
                val reminder = ExpiryReminder(
                    id = config.id,
                    days = newDays,
                    tag = config.tag,
                    createTime = config.createTime
                )
                database.expiryReminderDao().updateExpiryReminder(reminder)
            }
        }
        hideDialog()
    }

}