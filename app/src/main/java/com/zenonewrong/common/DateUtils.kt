package com.zenonewrong.common

import androidx.compose.ui.graphics.Color
import com.zenonewrong.bean.StatusCard
import com.zenonewrong.entity.ItemInfo
import com.zenonewrong.ui.theme.CardBlue
import com.zenonewrong.ui.theme.CardGreen
import com.zenonewrong.ui.theme.CardRed
import com.zenonewrong.ui.theme.CardYellow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

/**
 * 根据物品过期日期和状态卡片计算图标背景颜色
 * @param itemInfo 物品信息
 * @param statusCards 状态卡片列表，可以为空
 * @return 对应的颜色
 */
fun getIconBackgroundColor(itemInfo: ItemInfo, statusCards: List<StatusCard> = emptyList()): Color {
    return try {
        val today = LocalDate.now()
        val expiryDate = LocalDate.parse(itemInfo.maturityDate, DateTimeFormatter.ISO_LOCAL_DATE)
        val daysUntilExpiry = ChronoUnit.DAYS.between(today, expiryDate)

        // 如果statusCards为空，使用默认的日期逻辑
        if (statusCards.isEmpty()) {
            return when {
                daysUntilExpiry < 0 -> CardRed
                daysUntilExpiry <= 3 -> CardYellow
                daysUntilExpiry <= 7 -> CardBlue
                else -> CardGreen // 其他情况-绿色
            }
        }

        // 找到最匹配的状态卡片
        statusCards.find { card ->
            if(daysUntilExpiry<0){
                return@find true;
            }
            val cardDays = card.days.toIntOrNull() ?: return@find false
            return@find daysUntilExpiry < cardDays
        }?.color ?: CardGreen
    } catch (e: Exception) {
        CardGreen
    }
}

/**
 * 计算到期日期显示文本
 * @param maturityDate 到期日期字符串
 * @return 格式化的到期文本
 */
fun getExpiryText(maturityDate: String): String {
    return try {
        val today = LocalDate.now()
        val expiryDate = LocalDate.parse(maturityDate, DateTimeFormatter.ISO_LOCAL_DATE)
        val daysUntilExpiry = ChronoUnit.DAYS.between(today, expiryDate)

        when {
            daysUntilExpiry == 0L -> "今日到期"
            daysUntilExpiry == 1L -> "明日到期"
            daysUntilExpiry > 1L -> "还有${daysUntilExpiry}天到期"
            else -> "已过期${-daysUntilExpiry}天"
        }
    } catch (e: Exception) {
        maturityDate // 如果日期解析失败，返回原日期
    }
}