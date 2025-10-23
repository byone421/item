package com.zenonewrong.ui.screen.classify

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zenonewrong.Screen
import com.zenonewrong.entity.Classify
import com.zenonewrong.viewmodel.AppViewModel
import com.zenonewrong.ui.screen.components.SwipeableClassifyContainer
import com.zenonewrong.viewmodel.ClassifyViewModel
import com.zenonewrong.viewmodel.ClassifyViewModel.MessageEvent
import com.zenonewrong.viewmodel.ItemInfoViewModel

@Composable
@Preview
fun ClassifyScreen() {


    val appViewModel: AppViewModel =
        viewModel(viewModelStoreOwner = LocalActivity.current as ComponentActivity)
    val itemInfoVM: ItemInfoViewModel =
        viewModel(viewModelStoreOwner = LocalActivity.current as ComponentActivity)

    val sourceScreen = appViewModel.sourceScreen.collectAsState()
    val classifyVM: ClassifyViewModel = viewModel()
    val collectAsState by classifyVM.classifyState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val classifyList by classifyVM.allClassifies.collectAsState()

    // 删除确认对话框状态
    val showDeleteDialog = remember { mutableStateOf(false) }
    val classifyToDelete = remember { mutableStateOf<Classify?>(null) }

    LaunchedEffect(Unit) {
        classifyVM.messageEvent.collect { event ->
            when (event) {
                is MessageEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }

                is MessageEvent.Success -> {}
            }
        }
    }

    Scaffold(
        topBar = {
            ClassifyScreenTopBar(appViewModel, classifyVM)
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->
        LazyColumn(modifier = Modifier
            .padding(innerPadding)
            .padding(top = 8.dp)) {
            items(classifyList.size, key = { classifyList[it].id }) { index ->
                val classify = classifyList[index]

                SwipeableClassifyContainer(
                    classify = classify,
                    onDelete = {
                        classifyToDelete.value = classify
                        showDeleteDialog.value = true
                    },
                    onEdit = {
                        classifyVM.startEdit(it)
                    },
                    onDetail = {
                        // 点击分类跳转到SearchScreen，传递分类信息
                        appViewModel.navigateTo(
                            Screen.Home.route,
                            Screen.Search.route + "?title=${it.name}&classifyId=${it.id}"
                        )
                    }
                ) {
                    ClassifyItem(
                        item = classify,
                        index = index,
                        itemClick = {
                            if (Screen.AddItem.route == sourceScreen.value) {
                                itemInfoVM.updateClassify(it)
                                appViewModel.navigateBack()
                            }
                        }
                    )
                }
            }
        }
        if (collectAsState.isDialogVisible) {
            AlertDialog(
                onDismissRequest = { classifyVM.toggleDialog(false) },
                title = { Text(style = MaterialTheme.typography.titleMedium, text = "添加分类") },
                text = {
                    Column {
                        OutlinedTextField(
                            placeholder = { Text("请输入分类名称") },
                            value = collectAsState.name,
                            onValueChange = { if (it.length < 10) classifyVM.updateName(it) },
                            label = { Text("名称", style = MaterialTheme.typography.bodyMedium) }
                        )
                        OutlinedTextField(
                            placeholder = { Text("请输入分类排序") },
                            value = collectAsState.sortOrder,
                            onValueChange = { if (it.length < 10) classifyVM.updateSortOrder(it) },
                            label = { Text("排序", style = MaterialTheme.typography.bodyMedium) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("展示到首页", style = MaterialTheme.typography.bodyMedium)

                            androidx.compose.material3.Switch(
                                checked = collectAsState.showOnHome,
                                onCheckedChange = { classifyVM.updateShowOnHome(it) }
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(enabled = collectAsState.name.isNotBlank(), onClick = {
                        classifyVM.toggleDialog(false)
                        classifyVM.addClassify()
                    }) {
                        Text(
                            "保存",
                            color = MaterialTheme.colorScheme.secondary,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                })
        }

        // 删除确认对话框
        if (showDeleteDialog.value) {
            classifyToDelete.value?.let { classify ->
                AlertDialog(
                    onDismissRequest = {
                        showDeleteDialog.value = false
                        classifyToDelete.value = null
                    },
                    title = { Text("确认删除", style = MaterialTheme.typography.titleMedium) },
                    text = {
                        Text(
                            "你确定要删除分类 \"${classify.name}\" 吗？",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            classifyVM.deleteClassify(classify)
                            showDeleteDialog.value = false
                            classifyToDelete.value = null
                        }) {
                            Text(
                                "删除",
                                color = Color.Red,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            showDeleteDialog.value = false
                            classifyToDelete.value = null
                        }) {
                            Text("取消", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                )
            }
        }
    }


}


@Composable
private fun ClassifyItem(item: Classify, index: Int, itemClick: (Classify) -> Unit) {
    Card(
        onClick = {
            itemClick(item)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, bottom = 4.dp, end = 8.dp),
    ) {
        Row(
            modifier = Modifier
                .background(Color.White)
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${index + 1}.",
                color = Color.Black,
                fontSize = 16.sp,
                modifier = Modifier.weight(0.1f)
            )
            Text(
                text =
                    item.name,
                color = Color.Black,
                fontSize = 16.sp,
                modifier = Modifier.weight(0.6f)
            )
            Text(
                text = "${item.sortOrder}",
                color = Color.Gray,
                fontSize = 16.sp,
                modifier = Modifier
                    .weight(0.3f)
                    .wrapContentWidth(Alignment.End)
            )
        }
    }
}
