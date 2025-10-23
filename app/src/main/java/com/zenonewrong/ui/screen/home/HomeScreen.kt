package com.zenonewrong.ui.screen.home

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
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
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zenonewrong.entity.ItemInfo
import com.zenonewrong.viewmodel.HomeViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.style.TextOverflow
import com.zenonewrong.Screen
import com.zenonewrong.ui.screen.components.SearchBox
import com.zenonewrong.ui.screen.components.SwipeableItemContainer
import com.zenonewrong.common.getExpiryText
import com.zenonewrong.common.getIconBackgroundColor
import com.zenonewrong.ui.theme.CardGreen
import com.zenonewrong.viewmodel.AppViewModel

@Composable
fun HomeScreen() {
    val homeViewModel: HomeViewModel = viewModel()
    val activity = LocalActivity.current as ComponentActivity
    val appViewModel: AppViewModel = viewModel(viewModelStoreOwner = activity)
    val categories by homeViewModel.homeClassifies.collectAsState()
    val itemInfos by homeViewModel.itemInfos.collectAsState()
    val statusCards by homeViewModel.statusCards.collectAsState()
    val showDeleteDialog by homeViewModel.showDeleteDialog.collectAsState()
    val itemToDelete by homeViewModel.itemToDelete.collectAsState()

    // 监听配置更新事件，通知AppViewModel
    LaunchedEffect(Unit) {
        homeViewModel.refreshStatusCards()
    }
    Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
        SearchBox(onSearchClick = {
            appViewModel.navigateTo(Screen.Home.route, Screen.Search.route + "?title=搜索物品")
        }, readOnly = true)
        LazyColumn(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxSize(),
            contentPadding = PaddingValues(bottom = 56.dp)
        ) {
            //顶部统计
            item {
                StatusOverviewGrid(
                    statusCards = statusCards,
                    onCardClick = { statusCard ->
                        appViewModel.navigateTo(
                            Screen.Home.route,
                            Screen.Search.route + "?title=${statusCard.title}&days=${statusCard.days}"
                        )
                    }
                )
            }
            //我的分类
            if (categories.isNotEmpty()) {
                item {
                    //分类
                    CategoryView(
                        categories = categories,
                        onCategoryClick = { category ->
                            // 点击分类跳转到SearchScreen，传递分类信息
                            appViewModel.navigateTo(
                                Screen.Home.route,
                                Screen.Search.route + "?title=${category.name}&classifyId=${category.id}"
                            )
                        }
                    )
                }
            }
            //全部记录
            item {
                RecordTopBar(recordCount = itemInfos.size)
            }
            //全部记录-列表
            if(itemInfos.isEmpty()){
                item{
                    Box(modifier = Modifier.padding(vertical = 50.dp).fillMaxWidth(),contentAlignment = Alignment.Center) {
                        Text(
                            text = "暂无物品，快去添加吧",
                            fontSize = 16.sp
                        )
                    }
                }
            }
            items(
                count = itemInfos.size,
                key = { itemInfos[it].id }
            ) { index ->
                SwipeableItemContainer(
                    itemInfo = itemInfos[index],
                    onDelete = { item ->
                        homeViewModel.showDeleteConfirmDialog(item)
                    },
                    onEdit = { item ->
                        appViewModel.navigateTo(
                            Screen.Home.route,
                            Screen.AddItem.route + "?id=${item.id}"
                        )
                    },
                    onCopy = { item ->
                        appViewModel.navigateTo(
                            Screen.Home.route,
                            Screen.AddItem.route + "?id=${item.id}&copy=true"
                        )
                    }
                ) {
                    ItemInfoItem(
                        itemInfo = itemInfos[index],
                        iconBackgroundColor = getIconBackgroundColor(itemInfos[index], statusCards),
                        onItemClick = {
                            appViewModel.navigateTo(
                                Screen.Home.route,
                                Screen.AddItem.route + "?id=${it.id}"
                            )
                        }
                    )
                }
            }
        }
    }

    // 删除确认对话框
    if (showDeleteDialog) {
        itemToDelete?.let { item ->
            AlertDialog(
                onDismissRequest = {
                    homeViewModel.hideDeleteConfirmDialog()
                },
                title = {
                    Text(
                        text = "确认删除",
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                text = {
                    Text(
                        text = "确定要删除物品 \"${item.name}\" 吗？此操作不可撤销。",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            homeViewModel.confirmDelete()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text(
                            text = "删除",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            homeViewModel.hideDeleteConfirmDialog()
                        }
                    ) {
                        Text(
                            text = "取消",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun ItemInfoItem(
    itemInfo: ItemInfo,
    modifier: Modifier = Modifier,
    iconBackgroundColor: Color = CardGreen,
    onItemClick: (itemInfo: ItemInfo) -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable { onItemClick(itemInfo) },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 左侧图标区域
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        color = iconBackgroundColor,
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = itemInfo.name.first().toString(),
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
                // 物品名称
                Text(
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    text = itemInfo.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(4.dp))

                // 位置和数量信息
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (itemInfo.classifyName.isNotBlank()) {
                        Text(
                            modifier = Modifier.widthIn(max = 80.dp),
                            text = itemInfo.classifyName,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 14.sp,
                            color = Color.Black
                        )
                        Text(
                            text = " | ",
                            maxLines = 1,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                    Text(
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        text = getExpiryText(itemInfo.maturityDate),
                        fontSize = 14.sp,
                        color = iconBackgroundColor
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // 右侧过期日期
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "到期日期",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    text = itemInfo.maturityDate,
                    fontSize = 14.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.End,
                    modifier = Modifier.width(100.dp)
                )
            }
        }
    }
}
