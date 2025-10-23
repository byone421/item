package com.zenonewrong.bean

import androidx.compose.ui.graphics.Color
import com.zenonewrong.ui.theme.CardBlue
import com.zenonewrong.ui.theme.CardGreen
import com.zenonewrong.ui.theme.CardRed
import com.zenonewrong.ui.theme.CardYellow

data class StatusCard(
    var days: String,
    val title: String,
    val count: Int,
    val color: Color
)

