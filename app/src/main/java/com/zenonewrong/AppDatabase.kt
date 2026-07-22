package com.zenonewrong

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.zenonewrong.dao.ClassifyDao
import com.zenonewrong.dao.ExpiryReminderDao
import com.zenonewrong.dao.ItemInfoDao
import com.zenonewrong.entity.Classify
import com.zenonewrong.entity.ExpiryReminder
import com.zenonewrong.entity.ItemInfo

@Database(entities = [ItemInfo::class, Classify::class, ExpiryReminder::class], version = 5, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun itemInfoDao(): ItemInfoDao
    abstract fun classifyDao(): ClassifyDao
    abstract fun expiryReminderDao(): ExpiryReminderDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE item_info ADD COLUMN image_paths TEXT NOT NULL DEFAULT ''")
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                if (!db.hasColumn("classify", "show_on_home")) {
                    db.execSQL("ALTER TABLE classify ADD COLUMN show_on_home INTEGER NOT NULL DEFAULT 1")
                }
            }
        }

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                if (!db.hasColumn("classify", "description")) {
                    db.execSQL("ALTER TABLE classify ADD COLUMN description TEXT NOT NULL DEFAULT ''")
                }
            }
        }

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
                    .addMigrations(MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

private fun SupportSQLiteDatabase.hasColumn(table: String, column: String): Boolean {
    query("PRAGMA table_info($table)").use { cursor ->
        val nameIndex = cursor.getColumnIndex("name")
        while (cursor.moveToNext()) {
            if (cursor.getString(nameIndex) == column) return true
        }
    }
    return false
}
