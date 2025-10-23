package com.zenonewrong.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "item_info")
data class ItemInfo(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "produced_date") val producedDate: String,
    @ColumnInfo(name = "storage_duration") val storageDuration: String,
    @ColumnInfo(name = "storage_unit") val storageUnit: String,
    @ColumnInfo(name = "maturity_date") val maturityDate: String,
    @ColumnInfo(name = "classify_id") val classifyId: Long?,
    @ColumnInfo(name = "classify_name") val classifyName: String,
    @ColumnInfo(name = "purchase_price") val purchasePrice: String,
    @ColumnInfo(name = "purchase_date") val purchaseDate: String,
    @ColumnInfo(name = "storage_location") val storageLocation: String,
    @ColumnInfo(name = "storage_quantity") val storageQuantity: String,
    @ColumnInfo(name = "remark") val remark: String
)