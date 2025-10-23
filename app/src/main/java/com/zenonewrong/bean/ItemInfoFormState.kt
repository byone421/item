package com.zenonewrong.bean

import com.zenonewrong.entity.ItemInfo
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// 表单状态类
data class ItemFormState(
    val id: Long = 0,  // 用于区分新增/编辑
    val name: String = "",
    val producedDate: String = "",
    val storageDuration: String = "",
    val storageUnit: String = "天", // 默认单位
    val maturityDate: String = "",
    val classifyId: Long? = null,
    val classifyName: String = "",

    val purchaseDate: String = "",
    val purchasePrice: String = "",
    val storageLocation: String = "",
    val storageQuantity: String = "",
    val remark: String = "",

    // 验证状态
    val isNameValid: Boolean = true,
    val isMaturityDateValid: Boolean = true,
    // UI 状态
    val showDatePicker: Boolean = false,
    val showStorageUnit: Boolean = false,
    val currentDateField: DateFieldType? = null
) {
    enum class DateFieldType {
        PRODUCED_DATE, MATURITY_DATE, PURCHASE_DATE
    }

    // 转换为实体
    fun toEntity(): ItemInfo {
        return ItemInfo(
            id = if (id == 0L) 0 else id,
            name = name,
            producedDate = producedDate,
            storageDuration = storageDuration,
            storageUnit = storageUnit,
            maturityDate = maturityDate,
            classifyId = classifyId,
            classifyName = classifyName,
            purchaseDate = purchaseDate,
            storageLocation = storageLocation,
            storageQuantity = storageQuantity,
            purchasePrice = purchasePrice,
            remark = remark
        )
    }

    // 从实体加载
    companion object {
        fun fromEntity(entity: ItemInfo): ItemFormState {
            return ItemFormState(
                id = entity.id,
                name = entity.name,
                producedDate = entity.producedDate,
                storageDuration = entity.storageDuration.toString(),
                storageUnit = entity.storageUnit,
                maturityDate = entity.maturityDate,
                classifyId = entity.classifyId,
                classifyName = entity.classifyName,
                purchaseDate = entity.purchaseDate,
                storageLocation = entity.storageLocation,
                storageQuantity = entity.storageQuantity,
                remark = entity.remark
            )
        }
    }

    // 验证表单
    fun validate(): ItemFormState {
        return this.copy(
            isNameValid = name.isNotBlank(),
            isMaturityDateValid = maturityDate.isNotBlank() && isValidDate(maturityDate),
        )
    }

    private fun isValidDate(dateStr: String): Boolean {
        return try {
            LocalDate.parse(dateStr, DateTimeFormatter.ISO_DATE)
            true
        } catch (e: Exception) {
            false
        }
    }

    // 计算保质期到期日
    fun calculateMaturityDate(): ItemFormState {
        if (producedDate.isBlank() || storageDuration.isBlank() || storageUnit.isBlank()) return this

        return try {
            var duration = 0
            when(storageUnit){
                "天"->{duration = storageDuration.toInt()}
                "周"->{duration = (storageDuration.toInt())*7}
                "月"->{duration = (storageDuration.toInt())*30}
                "年"->{duration = (storageDuration.toInt())*365}
            }

            val produced = LocalDate.parse(producedDate, DateTimeFormatter.ISO_DATE)
            val maturity = produced.plusDays(duration.toLong()-1)

            this.copy(
                maturityDate = maturity.format(DateTimeFormatter.ISO_DATE),
                isMaturityDateValid = true
            )
        } catch (e: Exception) {
            this.copy(
                isMaturityDateValid = false,
            )
        }
    }
}