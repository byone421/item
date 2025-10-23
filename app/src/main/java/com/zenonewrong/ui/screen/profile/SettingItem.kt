package com.zenonewrong.ui.screen.profile


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zenonewrong.ui.theme.LineGrey
import com.zenonewrong.ui.theme.TextGrey

/**
 * 通用的设置项组件
 * @param title 标题文字
 * @param onClick 点击事件
 * @param trailingIcon 右侧图标，默认为箭头图标
 * @param modifier 修饰符
 */
@Composable
fun SettingItem(
    title: String,
    showHorizontalDivider: Boolean = true,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier.height(54.dp)
            .clickable{
                onClick()
            }
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(modifier= Modifier.widthIn(8.dp,8.dp))
        Text(title, style = MaterialTheme.typography.labelMedium)
        Spacer(modifier = Modifier.weight(1f,))
        Icon(
            modifier = Modifier.size(22.dp), imageVector =
                Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = TextGrey
        )
    }
    if(showHorizontalDivider){
        HorizontalDivider(
            color = LineGrey,
            thickness = 0.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp)
                .height(.5.dp)
        )
    }

}