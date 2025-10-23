package com.zenonewrong.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "classify")
data  class Classify (
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "sort") val sortOrder: Int,
    @ColumnInfo(name = "create_time") val createTime: Long,
    @ColumnInfo(name = "show_on_home") val showOnHome: Boolean = true
)