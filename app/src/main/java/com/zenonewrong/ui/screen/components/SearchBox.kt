package com.zenonewrong.ui.screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zenonewrong.R

//@Composable
//fun SearchBox(
//
//    onSearchClick: () -> Unit
//) {
//    Box(
//        modifier = Modifier
//            .background(MaterialTheme.colorScheme.secondary)
//            .padding(horizontal = 20.dp, vertical = 12.dp)
//            .height(48.dp),
//        contentAlignment = Alignment.Center
//    ) {
//        Surface(
//            modifier = Modifier
//                .fillMaxHeight()
//                .fillMaxWidth()
//                .clickable(onClick = onSearchClick),
//            shape = RoundedCornerShape(4.dp),
//        ) {
//            Row(
//                modifier = Modifier
//                    .height(40.dp)
//                    .fillMaxWidth()
//                    .background(MaterialTheme.colorScheme.secondaryContainer)
//                    .padding(horizontal = 4.dp),
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.Start,
//            ) {
//                // 搜索图标
//                Icon(
//                    imageVector = Icons.Default.Search,
//                    contentDescription = stringResource(R.string.search),
//                    tint = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f),
//                    modifier = Modifier.size(24.dp)
//                )
//                Spacer(modifier = Modifier.width(8.dp))
//                // 提示文本
//                Text(
//                    text = stringResource(R.string.search_tip),
//                    style = MaterialTheme.typography.bodyMedium,
//                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f),
//                    modifier = Modifier.weight(1f)
//                )
//            }
//        }
//    }
//
//}

/**
 * 可输入的SearchBox重载版本
 * @param value 当前输入值
 * @param onValueChange 输入值变化回调
 * @param onSearchClick 搜索图标点击回调（可选）
 * @param placeholder 提示文本
 */
@Composable
fun SearchBox(
    value: String = "",
    onValueChange: ((String) -> Unit) = {},
    onSearchClick: (() -> Unit)? = null,
    placeholder: String = "请输入搜索内容...",
    readOnly : Boolean = false
) {
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.secondary)
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .height(48.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .let { modifier ->
                    if (onSearchClick != null) {
                        modifier.clickable(onClick = onSearchClick)
                    } else {
                        modifier
                    }
                },
            shape = RoundedCornerShape(4.dp),
        ) {
            Row(
                modifier = Modifier
                    .height(40.dp)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
            ) {
                // 搜索图标
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(R.string.search),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                if(readOnly){
                    Text(
                        text = stringResource(R.string.search_tip),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f),
                        modifier = Modifier.weight(1f)
                    )
                }else{
                    BasicTextField(
                        value = value,
                        onValueChange = onValueChange,
                        modifier = Modifier.weight(1f),
                        textStyle = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        ),
                        decorationBox = { innerTextField ->
                            if (value.isEmpty()) {
                                Text(
                                    text = placeholder,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f)
                                )
                            }
                            innerTextField()
                        }
                    )
                }
            }
        }
    }
}