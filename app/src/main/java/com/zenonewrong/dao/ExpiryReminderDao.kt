package com.zenonewrong.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.zenonewrong.entity.ExpiryReminder
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpiryReminderDao {
    @Query("SELECT * FROM expiry_reminder ORDER BY create_time ASC")
    fun getAllExpiryReminders(): Flow<List<ExpiryReminder>>

    @Query("SELECT * FROM expiry_reminder WHERE id = :id")
    suspend fun getExpiryReminderById(id: Long): ExpiryReminder?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpiryReminder(expiryReminder: ExpiryReminder): Long

    @Update
    suspend fun updateExpiryReminder(expiryReminder: ExpiryReminder)

    @Delete
    suspend fun deleteExpiryReminder(expiryReminder: ExpiryReminder)

    @Query("DELETE FROM expiry_reminder WHERE id = :id")
    suspend fun deleteExpiryReminderById(id: Long)

    @Query("DELETE FROM expiry_reminder")
    suspend fun deleteAllExpiryReminders()

    @Query("SELECT COUNT(*) FROM item_info WHERE maturity_date < date('now')")
    suspend fun getExpiredItemCount(): Int


    @Query(
        """
        SELECT COUNT(*) 
        FROM item_info 
        WHERE maturity_date >= date('now') AND maturity_date <(
            SELECT date('now', '+' || expiry_reminder.days || ' days') 
            FROM expiry_reminder 
            WHERE expiry_reminder.tag = 'yellow'
        )
        """
    )
    suspend fun getExpiringIn3DaysItemCount(): Int

    @Query("SELECT COUNT(*) FROM item_info WHERE maturity_date > date('now', '+3 days') AND maturity_date <= date('now', '+7 days')")
    suspend fun getExpiringIn7DaysItemCount(): Int

    @Query("SELECT COUNT(*) FROM item_info WHERE maturity_date > date('now', '+7 days') AND maturity_date <= date('now', '+10 days')")
    suspend fun getExpiringIn10DaysItemCount(): Int


    @Query("""
            SELECT COUNT(*) 
            FROM item_info 
            WHERE maturity_date >= date('now') 
              AND maturity_date <= date('now', '+' || :days || ' days')
        """)
    suspend fun countItemsDueInDays(days: Int): Int

    @Query(
        """
        SELECT expiry_reminder.days
            FROM expiry_reminder 
            WHERE expiry_reminder.tag = :tag 
        """
    )
    suspend fun getExpiringDaysByTag(tag: String): Int
}