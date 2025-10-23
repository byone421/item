package com.zenonewrong.ui.screen.search

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import com.zenonewrong.ui.screen.components.SearchBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zenonewrong.R
import com.zenonewrong.Screen
//import com.zenonewrong.ui.screen.classify.tintedVectorPainter
import com.zenonewrong.ui.screen.components.SwipeableItemContainer
import com.zenonewrong.ui.screen.home.ItemInfoItem
import com.zenonewrong.common.getIconBackgroundColor
import com.zenonewrong.ui.theme.BGGrey
import com.zenonewrong.ui.theme.TextGrey
import com.zenonewrong.viewmodel.AppViewModel
import com.zenonewrong.viewmodel.SearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(title: String = "搜索", days: String? = null, classifyId: Long? = null) {
    val searchViewModel: SearchViewModel = viewModel()
    val appViewModel: AppViewModel = viewModel(viewModelStoreOwner = LocalActivity.current as ComponentActivity)

    // 搜索状态
    val searchQuery = remember { mutableStateOf("") }
    val itemInfos by searchViewModel.searchResults.collectAsState()
    val statusCards by searchViewModel.statusCards.collectAsState()

    // 删除对话框状态
    val showDeleteDialog = remember { mutableStateOf(false) }
    val itemToDelete = remember { mutableStateOf<com.zenonewrong.entity.ItemInfo?>(null) }

    // 删除所有过期物品对话框状态
    val showDeleteAllExpiredDialog = remember { mutableStateOf(false) }

    LaunchedEffect(Unit){
        searchViewModel.setSearchParams(searchQuery.value, days, classifyId)
    }
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(Unit) {
        searchViewModel.messageEvent.collect { event ->
            when (event) {
                is SearchViewModel.MessageEvent.ShowSnackbar ->  snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                },
                modifier = Modifier
                    .statusBarsPadding()
                    .height(50.dp)
                    .fillMaxWidth(),
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = { appViewModel.navigateBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    if("已过期" == title){
                        IconButton(onClick = {
                            showDeleteAllExpiredDialog.value = true
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "删除所有过期")
                        }
                    }

                }
            )
        }, snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            SearchBox(
                value = searchQuery.value,
                onValueChange = { query ->
                    searchQuery.value = query
                    searchViewModel.setSearchParams(query, days, classifyId)
                },
                placeholder = "请输入物品名称搜索"
            )
            if (itemInfos.isEmpty()){
                Box(modifier = Modifier.fillMaxSize(),contentAlignment = Alignment.Center) {
                    Text(
                        text = "暂无结果",
                        style =  MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }else{
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 4.dp)
                        .background(MaterialTheme.colorScheme.background),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    // 直接使用所有数据
                    items(
                        count = itemInfos.size,
                        key = { itemInfos[it].id }
                    ) { index ->

                        SwipeableItemContainer(
                            itemInfo = itemInfos[index],
                            onDelete = { item ->
                                itemToDelete.value = item
                                showDeleteDialog.value = true
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

        }
    }

    // 删除确认对话框
    if (showDeleteDialog.value) {
        itemToDelete.value?.let { item ->
            DeleteItemDialog(
                item = item,
                onDismiss = {
                    showDeleteDialog.value = false
                    itemToDelete.value = null
                },
                onConfirm = {
                    searchViewModel.deleteItem(item.id)
                    showDeleteDialog.value = false
                    itemToDelete.value = null
                }
            )
        }
    }

    // 删除所有过期物品确认对话框
    if (showDeleteAllExpiredDialog.value) {
        DeleteAllExpiredDialog(
            onDismiss = {
                showDeleteAllExpiredDialog.value = false
            },
            onConfirm = {
                searchViewModel.deleteAllExpiredItems()
                showDeleteAllExpiredDialog.value = false
            }
        )
    }
}