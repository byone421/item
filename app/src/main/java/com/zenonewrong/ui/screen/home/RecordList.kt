package com.zenonewrong.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zenonewrong.bean.Record

@Composable
fun RecordItem(
    record: Record,
    modifier: Modifier = Modifier,
    onItemClick: () -> Unit = {},
    onStatusClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onItemClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 左侧图标
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = record.iconColor,
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = record.iconText,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // 中间内容区域
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // 标题
                Text(
                    text = record.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(4.dp))

                // 状态信息
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${record.category} | ",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )

                    // 可点击的状态信息（蓝色下划线）
                    Text(
                        text = record.status,
                        fontSize = 14.sp,
                        color = Color.Blue,
                        modifier = Modifier.clickable { onStatusClick() },
                        textDecoration = TextDecoration.Underline
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // 右侧日期
            Text(
                text = record.date,
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = TextAlign.End,
                modifier = Modifier.width(100.dp)
            )
        }
    }
}

@Composable
fun RecordList(
    records: List<Record>,
    modifier: Modifier = Modifier,
    onItemClick: (Record) -> Unit = {},
    onStatusClick: (Record) -> Unit = {}
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(records.size) {
            RecordItem(
                record = records[it],
                onItemClick = { onItemClick(records[it]) },
                onStatusClick = { onStatusClick(records[it]) }
            )
        }
    }
}

@Composable
fun RecordTopBar(
    recordCount: Int,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = "我的物品（$recordCount）",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.align(Alignment.CenterStart)
        )
    }
}

//@Composable
//fun RecordListView() {
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color(0xFFF5F5F5))
//    ) {
//        // 顶部标题栏
//        RecordTopBar(recordCount = records.size)
//
//        // 记录列表
//        RecordList(
//            records = records,
//            onItemClick = { record ->
//                // 处理记录项点击
//                println("点击了记录: ${record.title}")
//            },
//            onStatusClick = { record ->
//                // 处理状态信息点击
//                println("点击了状态: ${record.status}")
//            }
//        )
//    }
//}