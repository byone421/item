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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zenonewrong.bean.Record
import com.zenonewrong.ui.theme.TextGrey

enum class RecordSortOption(val label: String) {
    CREATED_TIME("创建时间"),
    REMAINING_DAYS("剩余天数")
}

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
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(record.iconColor.copy(alpha = 0.16f), RoundedCornerShape(13.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = record.iconText,
                    color = record.iconColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = record.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D2930)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "${record.category} | ", fontSize = 14.sp, color = TextGrey)
                    Text(
                        text = record.status,
                        fontSize = 14.sp,
                        color = record.iconColor,
                        modifier = Modifier.clickable { onStatusClick() },
                        textDecoration = TextDecoration.Underline
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = record.date,
                fontSize = 12.sp,
                color = TextGrey,
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
            .background(Color(0xFFF7F0F5)),
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
    totalCount: Int = recordCount,
    selectedClassifyName: String? = null,
    onClearFilters: () -> Unit = {},
    sortOption: RecordSortOption = RecordSortOption.CREATED_TIME,
    onSortOptionSelected: (RecordSortOption) -> Unit = {}
) {
    var sortMenuExpanded by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 18.dp, top = 8.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = selectedClassifyName?.let { "$it · $recordCount 件" } ?: "全部物品 $totalCount",
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextGrey,
            )
            if (selectedClassifyName != null) {
                IconButton(
                    onClick = onClearFilters,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "取消分类筛选",
                        modifier = Modifier.size(16.dp),
                        tint = TextGrey
                    )
                }
            }
        }
        Box {
            TextButton(onClick = { sortMenuExpanded = true }) {
                Icon(
                    imageVector = Icons.Default.Sort,
                    contentDescription = "排序方式",
                    modifier = Modifier.size(18.dp),
                    tint = TextGrey
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = sortOption.label,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextGrey
                )
            }
            DropdownMenu(
                expanded = sortMenuExpanded,
                onDismissRequest = { sortMenuExpanded = false }
            ) {
                RecordSortOption.entries.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.label) },
                        onClick = {
                            onSortOptionSelected(option)
                            sortMenuExpanded = false
                        },
                        trailingIcon = {
                            if (option == sortOption) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null
                                )
                            }
                        }
                    )
                }
            }
        }
//        Text(
//            text = "全部 $totalCount",
//            fontSize = 13.sp,
//            fontWeight = FontWeight.ExtraBold,
//            color = Color(0xFF58A071),
//            modifier = Modifier.clickable(onClick = onClearFilters)
//        )
    }
}
