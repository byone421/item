package com.zenonewrong.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expiry_reminder")
data class ExpiryReminder(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "days")
    val days: Int,
    @ColumnInfo(name = "tag")
    val tag: String,
    @ColumnInfo(name = "create_time")
    val createTime: Long = System.currentTimeMillis()
)