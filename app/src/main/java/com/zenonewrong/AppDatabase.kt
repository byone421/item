package com.zenonewrong

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.zenonewrong.dao.ClassifyDao
import com.zenonewrong.dao.ExpiryReminderDao
import com.zenonewrong.dao.ItemInfoDao
import com.zenonewrong.entity.Classify
import com.zenonewrong.entity.ExpiryReminder
import com.zenonewrong.entity.ItemInfo

@Database(entities = [ItemInfo::class, Classify::class, ExpiryReminder::class], version = 2, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun itemInfoDao(): ItemInfoDao
    abstract fun classifyDao(): ClassifyDao
    abstract fun expiryReminderDao(): ExpiryReminderDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val roomCallback = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // 插入默认的过期提醒数据
                val currentTime = System.currentTimeMillis()
                db.execSQL("INSERT INTO expiry_reminder (id, days, tag, create_time) VALUES (1, 3, 'yellow', $currentTime)")
                db.execSQL("INSERT INTO expiry_reminder (id, days, tag, create_time) VALUES (2, 7, 'blue', $currentTime)")
                db.execSQL("INSERT INTO expiry_reminder (id, days, tag, create_time) VALUES (3, 10, 'green', $currentTime)")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).addCallback(roomCallback)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
