package com.zenonewrong.ui.screen.classify

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zenonewrong.entity.Classify
import com.zenonewrong.ui.screen.components.SwipeableClassifyContainer
import com.zenonewrong.ui.screen.components.SearchBox
import com.zenonewrong.ui.theme.LineGrey
import com.zenonewrong.viewmodel.AppViewModel
import com.zenonewrong.viewmodel.ClassifyViewModel
import com.zenonewrong.viewmodel.ClassifyViewModel.MessageEvent
import com.zenonewrong.viewmodel.ItemInfoViewModel

@Composable
fun ClassifyScreen(showBack: Boolean = false) {
    val activity = LocalActivity.current as ComponentActivity
    val appViewModel: AppViewModel = viewModel(viewModelStoreOwner = activity)
    val itemInfoVM: ItemInfoViewModel = viewModel(viewModelStoreOwner = activity)
    val classifyVM: ClassifyViewModel = viewModel()
    val formState by classifyVM.classifyState.collectAsState()
    val classifyList by classifyVM.allClassifies.collectAsState()
    val allItems by classifyVM.allItems.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val showDeleteDialog = remember { mutableStateOf(false) }
    val classifyToDelete = remember { mutableStateOf<Classify?>(null) }
    val counts = remember(allItems) { allItems.groupingBy { it.classifyId }.eachCount() }
    var query by remember { mutableStateOf("") }
    val filteredClassifies = remember(classifyList, query) {
        val keyword = query.trim()
        classifyList.filter {
            it.name.contains(keyword, ignoreCase = true) ||
                it.description.contains(keyword, ignoreCase = true)
        }
    }

    LaunchedEffect(Unit) {
        classifyVM.messageEvent.collect { event ->
            if (event is MessageEvent.ShowSnackbar) snackbarHostState.showSnackbar(event.message)
        }
    }

    fun openClassify(classify: Classify) {
        if (showBack) {
            itemInfoVM.updateClassify(classify)
            appViewModel.navigateBack()
        } else {
            appViewModel.showClassifyOnHome(classify.id, classify.name)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF7F0F5))
    ) {
        ClassifyScreenTopBar(appViewModel, classifyVM, showBack)
        Box(modifier = Modifier.weight(1f)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 72.dp)
            ) {
                item {
                    SearchBox(
                        value = query,
                        onValueChange = { query = it },
                        placeholder = "搜索分类",
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }

                item {
                    Text(
                        text = "分类管理",
                        color = Color(0xFF786F7D),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(start = 20.dp, top = 21.dp, bottom = 5.dp)
                    )
                }
                items(filteredClassifies.size, key = { filteredClassifies[it].id }) { index ->
                    val classify = filteredClassifies[index]
                    SwipeableClassifyContainer(
                        classify = classify,
                        onDelete = {
                            classifyToDelete.value = it
                            showDeleteDialog.value = true
                        },
                        onEdit = classifyVM::startEdit,
                        onDetail = ::openClassify
                    ) {
                        ClassifyItem(classify, counts[classify.id] ?: 0, ::openClassify)
                    }
                }
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter)
            )

            if (formState.isDialogVisible) {
                AlertDialog(
                    onDismissRequest = { classifyVM.toggleDialog(false) },
                    title = {
                        Text(
                            text = if (formState.editingClassify) "编辑分类" else "添加分类",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = formState.name,
                                onValueChange = { if (it.length < 10) classifyVM.updateName(it) },
                                label = { Text("名称") },
                                placeholder = { Text("请输入分类名称") },
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = formState.description,
                                onValueChange = { if (it.length <= 60) classifyVM.updateDescription(it) },
                                label = { Text("说明") },
                                placeholder = { Text("例如：厨房调味区") },
                                maxLines = 2
                            )
                            OutlinedTextField(
                                value = formState.sortOrder,
                                onValueChange = { if (it.length < 10) classifyVM.updateSortOrder(it) },
                                label = { Text("排序") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            enabled = formState.name.isNotBlank(),
                            onClick = {
                                classifyVM.toggleDialog(false)
                                classifyVM.addClassify()
                            }
                        ) { Text("保存", color = Color.White) }
                    },
                    dismissButton = {
                        TextButton(onClick = { classifyVM.toggleDialog(false) }) { Text("取消") }
                    }
                )
            }

            if (showDeleteDialog.value) {
                classifyToDelete.value?.let { classify ->
                    AlertDialog(
                        onDismissRequest = {
                            showDeleteDialog.value = false
                            classifyToDelete.value = null
                        },
                        title = { Text("确认删除") },
                        text = { Text("你确定要删除分类 \"${classify.name}\" 吗？") },
                        confirmButton = {
                            TextButton(onClick = {
                                classifyVM.deleteClassify(classify)
                                showDeleteDialog.value = false
                                classifyToDelete.value = null
                            }) { Text("删除", color = Color(0xFFC85D67)) }
                        },
                        dismissButton = {
                            TextButton(onClick = {
                                showDeleteDialog.value = false
                                classifyToDelete.value = null
                            }) { Text("取消") }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ClassifyItem(item: Classify, count: Int, onClick: (Classify) -> Unit) {
    Card(
        onClick = { onClick(item) },
        modifier = Modifier.fillMaxWidth().height(80.dp),
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(1.dp, LineGrey),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFAFD)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    color = Color(0xFF2D2930),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = item.description.ifBlank { "暂无分类说明" },
                    color = Color(0xFF786F7D),
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.size(16.dp))
            Column(
                modifier = Modifier.size(42.dp).background(Color(0xFFF7F0F5), RoundedCornerShape(14.dp)),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = count.toString(),
                    color = Color(0xFF2D2930),
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}
