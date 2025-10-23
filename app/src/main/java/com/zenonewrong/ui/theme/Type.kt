package com.zenonewrong.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ) ,

    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ) ,



    titleMedium=TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.sp,
        color = Color.Black
    ),


    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 24.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp,
        color = Color.Black
    ),

    displayMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 22.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.sp,
        color = Color.White
    ),

    displaySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.sp,
        color = TextGrey
    ),

    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 15.sp,
        letterSpacing = 0.sp,
        color = Color.Black
    ),

    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.sp,
        color = Color.Black
    ),

    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 17.sp,
        lineHeight = 17.sp,
        letterSpacing = 0.sp,
        color = Color.Black
    ),

)