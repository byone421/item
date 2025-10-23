package com.zenonewrong.bean

import androidx.compose.ui.graphics.Color

data class Record(
    val id: Int,
    val title: String,
    val date: String,
    val category: String,
    val status: String,
    val daysLeft: Int,
    val iconColor: Color,
    val iconText: String
)
