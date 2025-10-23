package com.zenonewrong.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zenonewrong.entity.Classify
import com.zenonewrong.entity.ItemInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemInfoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertData(itemInfo: ItemInfo): Long

    @Query("SELECT i.*, c.name as classify_name FROM item_info i LEFT JOIN classify c ON i.classify_id = c.id ORDER BY i.id DESC")
    fun getAllItemInfos(): Flow<List<ItemInfo>>

    @Query("SELECT * FROM item_info WHERE classify_id = :classifyId ORDER BY id DESC")
    fun getItemInfosByClassifyId(classifyId: Long): Flow<List<ItemInfo>>

    @Query("SELECT * FROM item_info WHERE id = :id")
    suspend fun findById(id: Long): ItemInfo?

    @Query("DELETE FROM item_info WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM item_info WHERE maturity_date < date('now')")
    suspend fun deleteAllExpiredItems()

    @Query("SELECT i.*, c.name as classify_name FROM item_info i LEFT JOIN classify c ON i.classify_id = c.id WHERE i.name LIKE :query ORDER BY i.id DESC")
    fun searchItemsByName(query: String): Flow<List<ItemInfo>>

    // 搜索已过期的物品
    @Query("SELECT i.*, c.name as classify_name FROM item_info i LEFT JOIN classify c ON i.classify_id = c.id WHERE i.maturity_date < date('now') ORDER BY i.id DESC")
    fun searchExpiredItems(): Flow<List<ItemInfo>>

    // 搜索已过期的物品（按名称）
    @Query("SELECT i.*, c.name as classify_name FROM item_info i LEFT JOIN classify c ON i.classify_id = c.id WHERE i.maturity_date < date('now') AND i.name LIKE :query ORDER BY i.id DESC")
    fun searchExpiredItemsByName(query: String): Flow<List<ItemInfo>>

    // 搜索指定天数内到期的物品
    @Query("SELECT i.*, c.name as classify_name FROM item_info i LEFT JOIN classify c ON i.classify_id = c.id WHERE i.maturity_date >= date('now') AND i.maturity_date < date('now', '+' || :days || ' days') ORDER BY i.id DESC")
    fun searchItemsDueInDays(days: Int): Flow<List<ItemInfo>>

    // 搜索指定天数内到期的物品（按名称）
    @Query("SELECT i.*, c.name as classify_name FROM item_info i LEFT JOIN classify c ON i.classify_id = c.id WHERE i.maturity_date >= date('now') AND i.maturity_date < date('now', '+' || :days || ' days') AND i.name LIKE :query ORDER BY i.id DESC")
    fun searchItemsDueInDaysByName(query: String, days: Int): Flow<List<ItemInfo>>

    // 按分类搜索物品
    @Query("SELECT i.*, c.name as classify_name FROM item_info i LEFT JOIN classify c ON i.classify_id = c.id WHERE i.classify_id = :classifyId ORDER BY i.id DESC")
    fun searchItemsByClassify(classifyId: Long): Flow<List<ItemInfo>>

    // 按分类搜索物品（按名称）
    @Query("SELECT i.*, c.name as classify_name FROM item_info i LEFT JOIN classify c ON i.classify_id = c.id WHERE i.classify_id = :classifyId AND i.name LIKE :query ORDER BY i.id DESC")
    fun searchItemsByClassifyByName(query: String, classifyId: Long): Flow<List<ItemInfo>>

    // 搜索指定分类的已过期物品
    @Query("SELECT i.*, c.name as classify_name FROM item_info i LEFT JOIN classify c ON i.classify_id = c.id WHERE i.classify_id = :classifyId AND i.maturity_date < date('now') ORDER BY i.id DESC")
    fun searchExpiredItemsByClassify(classifyId: Long): Flow<List<ItemInfo>>

    // 搜索指定分类的已过期物品（按名称）
    @Query("SELECT i.*, c.name as classify_name FROM item_info i LEFT JOIN classify c ON i.classify_id = c.id WHERE i.classify_id = :classifyId AND i.maturity_date < date('now') AND i.name LIKE :query ORDER BY i.id DESC")
    fun searchExpiredItemsByClassifyByName(query: String, classifyId: Long): Flow<List<ItemInfo>>

    // 搜索指定分类和天数内到期的物品
    @Query("SELECT i.*, c.name as classify_name FROM item_info i LEFT JOIN classify c ON i.classify_id = c.id WHERE i.classify_id = :classifyId AND i.maturity_date >= date('now') AND i.maturity_date < date('now', '+' || :days || ' days') ORDER BY i.id DESC")
    fun searchItemsDueInDaysByClassify(classifyId: Long, days: Int): Flow<List<ItemInfo>>

    // 搜索指定分类和天数内到期的物品（按名称）
    @Query("SELECT i.*, c.name as classify_name FROM item_info i LEFT JOIN classify c ON i.classify_id = c.id WHERE i.classify_id = :classifyId AND i.maturity_date >= date('now') AND i.maturity_date < date('now', '+' || :days || ' days') AND i.name LIKE :query ORDER BY i.id DESC")
    fun searchItemsDueInDaysByClassifyByName(query: String, classifyId: Long, days: Int): Flow<List<ItemInfo>>

    @Query("SELECT * FROM item_info ORDER BY id DESC")
    suspend fun getAllItems(): List<ItemInfo>

    @Query("DELETE FROM item_info")
    suspend fun deleteAll()

}