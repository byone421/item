package com.zenonewrong.ui.screen.iteminfo

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zenonewrong.R
import com.zenonewrong.Screen
import com.zenonewrong.bean.ItemFormState
import com.zenonewrong.entity.Classify
import com.zenonewrong.ui.theme.TextGrey
import com.zenonewrong.viewmodel.AppViewModel
import com.zenonewrong.viewmodel.ItemInfoViewModel.MessageEvent
import com.zenonewrong.viewmodel.ItemInfoViewModel

@Composable
fun AddItemScreen(itemId: Long? = null, isCopy: Boolean = false) {

    val appViewModel: AppViewModel =
        viewModel(viewModelStoreOwner = LocalActivity.current as ComponentActivity)
    val itemInfoVM: ItemInfoViewModel =
        viewModel(viewModelStoreOwner = LocalActivity.current as ComponentActivity)
    val formState by itemInfoVM.formState.collectAsState()
    BackHandler(
        enabled = true,
        onBack = {
            itemInfoVM.clearForm()
            appViewModel.navigateBack()
        }
    )
    // 如果有itemId，加载物品信息（只在初始化时加载一次）
    LaunchedEffect(itemId) {
        itemId?.let { id ->
            // 检查表单是否已经是空的状态，避免重复加载
            if (formState.name.isEmpty() && formState.classifyName.isEmpty()) {
                Log.d("AddItemScreen", "Loading item by id: $id")
                itemInfoVM.loadItemById(id)
            }
        }
    }
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(Unit) {
        itemInfoVM.messageEvent.collect { event ->
            when (event) {
                is MessageEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
                is MessageEvent.Success ->  {
                    itemInfoVM.clearForm()
                    appViewModel.navigateBack()
                }
            }
        }
    }
    Scaffold(
        topBar = {
            AddItemScreenTopBar(appViewModel,itemInfoVM)
        }, snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "基本信息",
                style = MaterialTheme.typography.labelSmall,
                color = TextGrey,
                fontSize = 17.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors
                    (containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, end = 4.dp)
                ) {
                    InputField(
                        required = true,
                        label = "名称",
                        text = formState.name,
                        onValueChange = { if (it.length <= 255) itemInfoVM.updateName(it) },
                        trailingContent = {
                            if (formState.name.isNotEmpty()) {
                                IconButton(onClick = {
                                    itemInfoVM.updateName("")
                                }, modifier = Modifier.size(40.dp)) {
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = stringResource(R.string.clear)
                                    )
                                }
                            }
                        }
                    )
                    InputField(
                        label = "生产日期",
                        text = formState.producedDate,
                        readOnly = true,
                        onValueChange = {},
                        onClick = {
                            itemInfoVM.showDatePicker(ItemFormState.DateFieldType.PRODUCED_DATE)
                        },
                        trailingContent = {
                            if (formState.producedDate.isNotEmpty()) {
                                IconButton(onClick = {
                                    itemInfoVM.updateProducedDate("")
                                }, modifier = Modifier.size(40.dp)) {
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = stringResource(R.string.clear)
                                    )
                                }
                            }
                        }
                    )
                    InputField(
                        label = "保存时长",
                        text = formState.storageDuration,
                        onValueChange = {
                            if("年" == formState.storageUnit){
                                if (it.length <= 3) itemInfoVM.updateStorageDuration(it)
                            }else{
                                if (it.length <= 5){
                                    itemInfoVM.updateStorageDuration(it)
                                }
                            }

                        },
                        keyboardType = KeyboardType.Number,
                        trailingContent = {
                            TextButton(onClick = {
                                itemInfoVM.showStorageUnit()
                            }, modifier = Modifier.size(40.dp)) {
                                Row {
                                    Text(
                                        fontSize = 14.sp,
                                        text = formState.storageUnit,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    )
                    InputField(
                        required = true,
                        label = "到期日期",
                        text = formState.maturityDate,
                        readOnly = true,
                        onValueChange = {},
                        onClick = {
                            itemInfoVM.showDatePicker(ItemFormState.DateFieldType.MATURITY_DATE)
                        }
                    )
                    InputField(
                        label = "分类",
                        text = formState.classifyName,
                        showHorizontalDivider = false,
                        readOnly = true,
                        onValueChange = {},
                        onClick = {
                            appViewModel.navigateTo(Screen.AddItem.route, Screen.Classify.route)
                        },
                        trailingContent = {
                            if (formState.classifyName.isNotEmpty()) {
                                IconButton(onClick = {
                                    itemInfoVM.updateClassify(Classify(0L, "", 0, 0L))
                                }, modifier = Modifier.size(40.dp)) {
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = "清除"
                                    )
                                }
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "其他信息",
                style = MaterialTheme.typography.labelSmall,
                color = TextGrey,
                fontSize = 17.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp)
                ) {
                    InputField(
                        label = "购买日期",
                        text = formState.purchaseDate,
                        readOnly = true,
                        onValueChange = {},
                        onClick = {
                            itemInfoVM.showDatePicker(ItemFormState.DateFieldType.PURCHASE_DATE)
                        },
                        trailingContent = {
                            if (formState.purchaseDate.isNotEmpty()) {
                                IconButton(onClick = {
                                    itemInfoVM.updatePurchaseDate("")
                                }, modifier = Modifier.size(40.dp)) {
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = "清除"
                                    )
                                }
                            }
                        }
                    )
                    InputField(
                        label = "存放位置",
                        text = formState.storageLocation,
                        onValueChange = {
                            if (it.length <= 255) itemInfoVM.updateStorageLocation(it)
                        },
                        trailingContent = {
                            if (formState.storageLocation.isNotEmpty()) {
                                IconButton(onClick = {
                                    itemInfoVM.updateStorageLocation("")
                                }, modifier = Modifier.size(40.dp)) {
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = "清除"
                                    )
                                }
                            }
                        }
                    )
                    InputField(
                        label = "购买金额",
                        text = formState.purchasePrice,
                        keyboardType = KeyboardType.Number,
                        onValueChange = {
                            if (it.length <= 10) itemInfoVM.updatePurchasePrice(it)
                        },
                        trailingContent = {
                            if (formState.purchasePrice.isNotEmpty()) {
                                IconButton(onClick = {
                                    itemInfoVM.updatePurchasePrice("")
                                }, modifier = Modifier.size(40.dp)) {
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = "清除"
                                    )
                                }
                            }
                        }
                    )
                    InputField(
                        label = "存放数量",
                        text = formState.storageQuantity,
                        onValueChange = {
                            if (it.length <= 10) itemInfoVM.updateStorageQuantity(it)
                        },
                        trailingContent = {
                            if (formState.storageQuantity.isNotEmpty()) {
                                IconButton(onClick = {
                                    itemInfoVM.updateStorageQuantity("")
                                }, modifier = Modifier.size(40.dp)) {
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = "清除"
                                    )
                                }
                            }
                        }
                    )
                    InputField(
                        label = "备注",
                        text = formState.remark,
                        showHorizontalDivider = false,
                        onValueChange = {
                            if (it.length <= 255) itemInfoVM.updateRemark(it)
                        },
                        trailingContent = {
                            if (formState.remark.isNotEmpty()) {
                                IconButton(onClick = {
                                    itemInfoVM.updateRemark("")
                                }, modifier = Modifier.size(40.dp)) {
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = "清除"
                                    )
                                }
                            }
                        }
                    )

                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 添加按钮
            AddButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    if (isCopy) {
                        itemInfoVM.clearIdForCopy()
                    }
                    itemInfoVM.saveItem()
                }
            )
            // 日期选择期
            if (formState.showDatePicker) {
                ShowDatePicker(itemInfoVM)
            }
            // 日期单位选择
            if (formState.showStorageUnit) {
                TimeUnitSelector(itemInfoVM)
            }

        }
    }
}


@Composable
fun AddButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.surface
        ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(32.dp)
    ) {
        Text(
            text = "保存",
            style = MaterialTheme.typography.labelMedium,
            color = Color.White
        )
    }
}