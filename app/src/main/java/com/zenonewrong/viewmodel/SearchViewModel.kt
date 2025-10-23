package com.zenonewrong.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zenonewrong.AppDatabase
import com.zenonewrong.bean.StatusCard
import com.zenonewrong.dao.ExpiryReminderDao
import com.zenonewrong.entity.ItemInfo
import com.zenonewrong.ui.theme.CardBlue
import com.zenonewrong.ui.theme.CardGreen
import com.zenonewrong.ui.theme.CardRed
import com.zenonewrong.ui.theme.CardYellow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SearchViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val itemInfoDao = database.itemInfoDao()
    private val expiryReminderDao: ExpiryReminderDao = database.expiryReminderDao()

    // 状态卡片数据
    private val _statusCards = MutableStateFlow<List<StatusCard>>(emptyList())
    val statusCards: StateFlow<List<StatusCard>> = _statusCards.asStateFlow()

    // 搜索参数
    private val _searchQuery = MutableStateFlow("")
    private val _daysFilter = MutableStateFlow<String?>(null)
    private val _classifyFilter = MutableStateFlow<Long?>(null)

    private val _messageEvent = MutableSharedFlow<MessageEvent>()
    val messageEvent = _messageEvent.asSharedFlow()


    sealed class MessageEvent {
        data class ShowSnackbar(val message: String) : MessageEvent()
    }

    // 搜索结果 - 使用Flow自动更新
    @OptIn(ExperimentalCoroutinesApi::class)
    val searchResults: StateFlow<List<ItemInfo>> = combine(
        _searchQuery,
        _daysFilter,
        _classifyFilter
    ) { query, days, classifyId ->
        Triple(query, days, classifyId)
    }.flatMapLatest { (query, days, classifyId) ->
        getSearchResultsFlow(query, days, classifyId)
    }.stateIn(
        scope = viewModelScope,
        started = kotlinx.coroutines.flow.SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    init{
        loadStatusCards()
    }

    // 设置搜索参数
    fun setSearchParams(query: String, days: String? = null, classifyId: Long? = null) {
        _searchQuery.value = query
        _daysFilter.value = days
        _classifyFilter.value = classifyId
    }

    // 获取搜索结果的Flow
    private fun getSearchResultsFlow(query: String, days: String? = null, classifyId: Long? = null): Flow<List<ItemInfo>> {
        return flow {
            try {
                val results = when {
                    // 如果有分类ID，优先按分类搜索
                    classifyId != null -> {
                        when {
                            // 分类 + 天数条件搜索
                            !days.isNullOrBlank() -> {
                                when (days) {
                                    "!" -> {
                                        // 分类 + 已过期物品
                                        if (query.isBlank()) {
                                            emitAll(itemInfoDao.searchExpiredItemsByClassify(classifyId))
                                        } else {
                                            emitAll(itemInfoDao.searchExpiredItemsByClassifyByName("%$query%", classifyId))
                                        }
                                    }
                                    else -> {
                                        // 分类 + 指定天数内到期物品
                                        val daysInt = days.toIntOrNull()
                                        if (daysInt != null && daysInt > 0) {
                                            if (query.isBlank()) {
                                                emitAll(itemInfoDao.searchItemsDueInDaysByClassify(classifyId, daysInt))
                                            } else {
                                                emitAll(itemInfoDao.searchItemsDueInDaysByClassifyByName("%$query%", classifyId, daysInt))
                                            }
                                        } else {
                                            // 如果无法解析天数，仅按分类搜索
                                            if (query.isBlank()) {
                                                emitAll(itemInfoDao.searchItemsByClassify(classifyId))
                                            } else {
                                                emitAll(itemInfoDao.searchItemsByClassifyByName("%$query%", classifyId))
                                            }
                                        }
                                    }
                                }
                            }
                            // 仅按分类搜索
                            query.isBlank() -> emitAll(itemInfoDao.searchItemsByClassify(classifyId))
                            else -> emitAll(itemInfoDao.searchItemsByClassifyByName("%$query%", classifyId))
                        }
                    }
                    // 根据天数进行条件搜索（无分类）
                    !days.isNullOrBlank() -> {
                        when (days) {
                            "!" -> {
                                // 已过期物品
                                if (query.isBlank()) {
                                    emitAll(itemInfoDao.searchExpiredItems())
                                } else {
                                    emitAll(itemInfoDao.searchExpiredItemsByName("%$query%"))
                                }
                            }
                            else -> {
                                // 指定天数内到期物品
                                val daysInt = days.toIntOrNull()
                                if (daysInt != null && daysInt > 0) {
                                    if (query.isBlank()) {
                                        emitAll(itemInfoDao.searchItemsDueInDays(daysInt))
                                    } else {
                                        emitAll(itemInfoDao.searchItemsDueInDaysByName("%$query%", daysInt))
                                    }
                                } else {
                                    // 如果无法解析天数，使用普通搜索
                                    if (query.isBlank()) {
                                        emit(emptyList())
                                    } else {
                                        emitAll(itemInfoDao.searchItemsByName("%$query%"))
                                    }
                                }
                            }
                        }
                    }
                    // 普通搜索
                    query.isBlank() -> emit(emptyList())
                    else -> emitAll(itemInfoDao.searchItemsByName("%$query%"))
                }
            } catch (e: Exception) {
                emit(emptyList())
            }
        }
    }

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
                    StatusCard("7", "7天内到期", 0, CardBlue),
                    StatusCard("10", "10天内到期", 0, CardGreen)
                )
                _statusCards.value = defaultCards
            }
        }
    }

    // 删除物品
    fun deleteItem(itemId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                itemInfoDao.deleteById(itemId)
                // 删除后刷新状态卡片
                loadStatusCards()
            } catch (e: Exception) {
                _messageEvent.emit(MessageEvent.ShowSnackbar("删除失败: ${e.message}"))
            }
        }
    }

    // 删除所有过期物品
    fun deleteAllExpiredItems() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                itemInfoDao.deleteAllExpiredItems()
                // 删除后刷新状态卡片
                loadStatusCards()
                _messageEvent.emit(MessageEvent.ShowSnackbar("删除成功"))
            } catch (e: Exception) {
                _messageEvent.emit(MessageEvent.ShowSnackbar("删除失败: ${e.message}"))
            }
        }
    }
}