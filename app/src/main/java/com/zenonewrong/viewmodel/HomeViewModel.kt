package com.zenonewrong.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zenonewrong.AppDatabase
import com.zenonewrong.bean.StatusCard
import com.zenonewrong.dao.ExpiryReminderDao
import com.zenonewrong.ui.theme.CardBlue
import com.zenonewrong.ui.theme.CardGreen
import com.zenonewrong.ui.theme.CardRed
import com.zenonewrong.ui.theme.CardYellow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.zenonewrong.entity.ItemInfo

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val classifyDao = database.classifyDao()
    private val itemInfoDao = database.itemInfoDao()
    private val expiryReminderDao: ExpiryReminderDao = database.expiryReminderDao()

    // 获取显示在首页的分类
    val homeClassifies =
        classifyDao.getHomeClassifies().stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // 直接加载所有物品信息
    val itemInfos =
        itemInfoDao.getAllItemInfos().stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // 状态卡片数据
    private val _statusCards = MutableStateFlow<List<StatusCard>>(emptyList())
    val statusCards: StateFlow<List<StatusCard>> = _statusCards.asStateFlow()

    // 删除对话框状态
    private val _showDeleteDialog = MutableStateFlow(false)
    val showDeleteDialog: StateFlow<Boolean> = _showDeleteDialog.asStateFlow()

    private val _itemToDelete = MutableStateFlow<ItemInfo?>(null)
    val itemToDelete: StateFlow<ItemInfo?> = _itemToDelete.asStateFlow()

    private fun loadStatusCards() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val yellowDays = expiryReminderDao.getExpiringDaysByTag("yellow")
                val blueDays = expiryReminderDao.getExpiringDaysByTag("blue")
                val greenDays = expiryReminderDao.getExpiringDaysByTag("green")

                val expiredCount = expiryReminderDao.getExpiredItemCount()
                val expiringIn3DaysCount = expiryReminderDao.countItemsDueInDays(yellowDays - 1)
                val expiringIn7DaysCount = expiryReminderDao.countItemsDueInDays(blueDays - 1)
                val expiringIn10DaysCount = expiryReminderDao.countItemsDueInDays(greenDays - 1)

                val cards = listOf(
                    StatusCard("!", "已过期", expiredCount, CardRed),
                    StatusCard(
                        yellowDays.toString(),
                        "${yellowDays}天内到期",
                        expiringIn3DaysCount,
                        CardYellow
                    ),
                    StatusCard(
                        blueDays.toString(),
                        "${blueDays}天内到期",
                        expiringIn7DaysCount,
                        CardBlue
                    ),
                    StatusCard(
                        greenDays.toString(),
                        "${greenDays}天内到期",
                        expiringIn10DaysCount,
                        CardGreen
                    )
                )
                _statusCards.value = cards
            } catch (e: Exception) {
                // 如果查询失败，使用默认数据
                val defaultCards = listOf(
                    StatusCard("!", "已过期", 0, CardRed),
                    StatusCard("3", "3天内到期", 0, CardYellow),
                    StatusCard("7", "7天后到期", 0, CardBlue),
                    StatusCard("10", "10天后到期", 0, CardGreen)
                )
                _statusCards.value = defaultCards
            }
        }
    }

    fun refreshStatusCards() {
        loadStatusCards()
    }

    // 删除物品
    fun deleteItem(itemId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                itemInfoDao.deleteById(itemId)
                refreshStatusCards()
            } catch (e: Exception) {}
        }
    }

    // 显示删除对话框
    fun showDeleteConfirmDialog(item: ItemInfo) {
        _itemToDelete.value = item
        _showDeleteDialog.value = true
    }

    // 隐藏删除对话框
    fun hideDeleteConfirmDialog() {
        _showDeleteDialog.value = false
        _itemToDelete.value = null
    }

    // 确认删除物品
    fun confirmDelete() {
        _itemToDelete.value?.let { item ->
            deleteItem(item.id)
        }
        hideDeleteConfirmDialog()
    }
}