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
){
    companion object {
        /**
         * 获取表的字段列表
         * @return 包含所有字段名的List<String>
         */
        fun getTableColumns(): List<String> {
            return listOf(
                "id",
                "分类名称",
                "排序",
                "创建时间",
                "是否在首页显示"
            )
        }
    }
}