package com.zenonewrong.ui.screen.home

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zenonewrong.Screen
import com.zenonewrong.common.getIconBackgroundColor
import com.zenonewrong.entity.ItemInfo
import com.zenonewrong.ui.screen.components.SearchBox
import com.zenonewrong.ui.screen.components.SwipeableItemContainer
import com.zenonewrong.ui.theme.CardGreen
import com.zenonewrong.ui.theme.LineGrey
import com.zenonewrong.viewmodel.AppViewModel
import com.zenonewrong.viewmodel.HomeViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

@Composable
fun HomeScreen() {
    val homeViewModel: HomeViewModel = viewModel()
    val activity = LocalActivity.current as ComponentActivity
    val appViewModel: AppViewModel = viewModel(viewModelStoreOwner = activity)
    val itemInfos by homeViewModel.itemInfos.collectAsState()
    val statusCards by homeViewModel.statusCards.collectAsState()
    val showDeleteDialog by homeViewModel.showDeleteDialog.collectAsState()
    val itemToDelete by homeViewModel.itemToDelete.collectAsState()
    var query by remember { mutableStateOf("") }
    var selectedDays by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) { homeViewModel.refreshStatusCards() }

    val filteredItems = remember(
        itemInfos,
        query,
        selectedDays,
        appViewModel.selectedHomeClassifyId
    ) {
        val normalizedQuery = query.trim().lowercase(Locale.getDefault())
        itemInfos.filter { item ->
            val matchesClassify = appViewModel.selectedHomeClassifyId == null ||
                item.classifyId == appViewModel.selectedHomeClassifyId
            val matchesQuery = normalizedQuery.isBlank() ||
                "${item.name}${item.classifyName}${item.storageLocation}"
                    .lowercase(Locale.getDefault())
                    .contains(normalizedQuery)
            matchesClassify && matchesQuery && matchesExpiry(item, selectedDays)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F0F5)),
        contentPadding = PaddingValues(bottom = 72.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(start = 18.dp, end = 18.dp, top = 16.dp)) {
                Text(
                    text = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy年M月d日")),
                    color = Color(0xFF786F7D),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = "物品记",
                    color = Color(0xFF2D2930),
                    fontSize = 34.sp,
                    lineHeight = 37.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.height(14.dp))
            }
        }
        item {
            SearchBox(
                value = query,
                onValueChange = { query = it },
                placeholder = "搜索物品、分类、位置",
                modifier = Modifier.padding(vertical = 0.dp)
            )
        }
        item {
            StatusOverviewGrid(
                statusCards = statusCards,
                selectedDays = selectedDays,
                onCardClick = { selectedDays = it.days }
            )
        }
        item {
            RecordTopBar(
                recordCount = filteredItems.size,
                totalCount = itemInfos.size,
                selectedClassifyName = appViewModel.selectedHomeClassifyName,
                onClearFilters = {
                    query = ""
                    selectedDays = null
                    appViewModel.clearHomeClassify()
                }
            )
        }
        if (filteredItems.isEmpty()) {
            item {
                Text(
                    text = "没有找到匹配的物品，换个关键词或分类试试。",
                    color = Color(0xFF786F7D),
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    modifier = Modifier
                        .padding(horizontal = 18.dp, vertical = 10.dp)
                        .fillMaxWidth()
                        .background(Color(0xFFFFFAFD), RoundedCornerShape(22.dp))
                        .padding(22.dp)
                )
            }
        }
        items(count = filteredItems.size, key = { filteredItems[it].id }) { index ->
            val item = filteredItems[index]
            SwipeableItemContainer(
                itemInfo = item,
                onDelete = homeViewModel::showDeleteConfirmDialog,
                onEdit = {
                    appViewModel.navigateTo(Screen.Home.route, Screen.AddItem.route + "?id=${it.id}")
                },
                onCopy = {
                    appViewModel.navigateTo(Screen.Home.route, Screen.AddItem.route + "?id=${it.id}&copy=true")
                }
            ) {
                ItemInfoItem(
                    itemInfo = item,
                    iconBackgroundColor = getIconBackgroundColor(item, statusCards),
                    onItemClick = {
                        appViewModel.navigateTo(Screen.Home.route, Screen.AddItem.route + "?id=${it.id}")
                    }
                )
            }
        }
    }

    if (showDeleteDialog) {
        itemToDelete?.let { item ->
            AlertDialog(
                onDismissRequest = homeViewModel::hideDeleteConfirmDialog,
                title = { Text("确认删除", style = MaterialTheme.typography.titleMedium) },
                text = { Text("确定要删除物品 \"${item.name}\" 吗？此操作不可撤销。") },
                confirmButton = {
                    Button(
                        onClick = homeViewModel::confirmDelete,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC85D67))
                    ) { Text("删除", color = Color.White) }
                },
                dismissButton = {
                    TextButton(onClick = homeViewModel::hideDeleteConfirmDialog) { Text("取消") }
                }
            )
        }
    }
}

private fun matchesExpiry(item: ItemInfo, selectedDays: String?): Boolean {
    if (selectedDays == null) return true
    return runCatching {
        val days = ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.parse(item.maturityDate))
        if (selectedDays.toLongOrNull() == null) days < 0
        else days in 0..selectedDays.toLong()
    }.getOrDefault(false)
}

@Composable
fun ItemInfoItem(
    itemInfo: ItemInfo,
    modifier: Modifier = Modifier,
    iconBackgroundColor: Color = CardGreen,
    onItemClick: (ItemInfo) -> Unit = {}
) {
    val remainingDays = runCatching {
        ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.parse(itemInfo.maturityDate))
    }.getOrNull()
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(96.dp)
            .clickable { onItemClick(itemInfo) },
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, LineGrey),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFAFD))
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(iconBackgroundColor.copy(alpha = 0.16f), RoundedCornerShape(13.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = itemInfo.name.take(1).ifBlank { "#" },
                    color = iconBackgroundColor,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            Spacer(modifier = Modifier.width(11.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = itemInfo.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color(0xFF2D2930),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = listOf(itemInfo.classifyName, itemInfo.storageLocation)
                        .filter { it.isNotBlank() }.joinToString(" · ").ifBlank { "未分类" },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color(0xFF786F7D),
                    fontSize = 13.sp
                )
                Text(
                    text = "到期 ${itemInfo.maturityDate}",
                    color = Color(0xFF786F7D),
                    fontSize = 13.sp
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = remainingDays?.let { "${kotlin.math.abs(it)}天" } ?: "--",
                    color = iconBackgroundColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.End
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (remainingDays != null && remainingDays < 0) "已过期" else "剩余",
                    color = Color(0xFF786F7D),
                    fontSize = 12.sp
                )
            }
        }
    }
}
