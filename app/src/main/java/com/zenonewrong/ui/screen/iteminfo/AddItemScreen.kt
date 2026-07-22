package com.zenonewrong.ui.screen.iteminfo

import android.net.Uri
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.FilledIconButton
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.zenonewrong.R
import com.zenonewrong.Screen
import com.zenonewrong.bean.ItemFormState
import com.zenonewrong.entity.Classify
import com.zenonewrong.ui.theme.LineGrey
import com.zenonewrong.ui.theme.TextGrey
import com.zenonewrong.viewmodel.AppViewModel
import com.zenonewrong.viewmodel.ItemInfoViewModel.MessageEvent
import com.zenonewrong.viewmodel.ItemInfoViewModel
import java.io.File

@Composable
fun AddItemScreen(itemId: Long? = null, isCopy: Boolean = false) {

    val appViewModel: AppViewModel =
        viewModel(viewModelStoreOwner = LocalActivity.current as ComponentActivity)
    val itemInfoVM: ItemInfoViewModel =
        viewModel(viewModelStoreOwner = LocalActivity.current as ComponentActivity)
    val formState by itemInfoVM.formState.collectAsState()
    val context = LocalContext.current
    var cameraImageUri by remember { mutableStateOf<Uri?>(null) }
    var previewImageIndex by remember { mutableStateOf<Int?>(null) }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 5)
    ) { uris ->
        itemInfoVM.addImages(uris)
    }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            cameraImageUri?.let { itemInfoVM.addImage(it) }
        }
    }
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
        containerColor = Color(0xFFF7F0F5),
        topBar = {
            AddItemScreenTopBar(appViewModel,itemInfoVM)
        }, snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF7F0F5))
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(start = 18.dp, end = 18.dp, bottom = 24.dp)
        ) {
            Text(
                text = "基本信息",
                color = TextGrey,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                border = BorderStroke(1.dp, LineGrey),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                colors = CardDefaults.cardColors
                    (containerColor = Color(0xFFFFFAFD))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
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
                        },
                        trailingContent = {
                            Text(
                                text = "自动计算",
                                color = Color(0xFF8D6AA5),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier
                                    .background(Color(0xFFEEE5F4), RoundedCornerShape(15.dp))
                                    .padding(horizontal = 10.dp, vertical = 7.dp)
                            )
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

            Spacer(modifier = Modifier.height(21.dp))
            Text(
                text = "其他信息",
                color = TextGrey,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                border = BorderStroke(1.dp, LineGrey),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFAFD))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
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

            Spacer(modifier = Modifier.height(21.dp))
            Text(
                text = "图片",
                color = TextGrey,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                border = BorderStroke(1.dp, LineGrey),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFAFD))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(formState.imagePaths) { path ->
                            val index = formState.imagePaths.indexOf(path)
                            Box {
                                AsyncImage(
                                    model = File(path),
                                    contentDescription = "物品图片",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(84.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { previewImageIndex = index }
                                )
                                FilledIconButton(
                                    onClick = { itemInfoVM.removeImage(path) },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(4.dp)
                                        .size(28.dp),
                                    colors = IconButtonDefaults.filledIconButtonColors(
                                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                                        contentColor = MaterialTheme.colorScheme.onSurface
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = "删除图片",
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                    if (formState.imagePaths.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            enabled = formState.imagePaths.size < 5,
                            modifier = Modifier.weight(1f).height(48.dp),
                            onClick = {
                                val uri = createCameraImageUri(context)
                                cameraImageUri = uri
                                cameraLauncher.launch(uri)
                            },
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(Icons.Filled.CameraAlt, contentDescription = null)
                            Spacer(modifier = Modifier.size(8.dp))
                            Text(
                                text = "拍照",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White,
                            )
                        }
                        Button(
                            enabled = formState.imagePaths.size < 5,
                            modifier = Modifier.weight(1f).height(48.dp),
                            onClick = {
                                imagePickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.background,
                                contentColor = MaterialTheme.colorScheme.onSurface
                            )
                        ) {

                            Icon(Icons.Filled.AddPhotoAlternate, contentDescription = null)
                            Spacer(modifier = Modifier.size(8.dp))
                            Text(
                                text = "图片",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                    Text(
                        text = "${formState.imagePaths.size}/5",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextGrey,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

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
            previewImageIndex?.let { index ->
                ImagePreviewDialog(
                    imagePaths = formState.imagePaths,
                    initialPage = index,
                    onDismiss = { previewImageIndex = null }
                )
            }

        }
    }
}

@Composable
private fun ImagePreviewDialog(
    imagePaths: List<String>,
    initialPage: Int,
    onDismiss: () -> Unit
) {
    val pagerState = rememberPagerState(
        initialPage = initialPage.coerceIn(imagePaths.indices),
        pageCount = { imagePaths.size }
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .clickable(onClick = onDismiss)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                AsyncImage(
                    model = File(imagePaths[page]),
                    contentDescription = "查看图片",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Text(
                text = "${pagerState.currentPage + 1}/${imagePaths.size}",
                color = Color.White,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 32.dp)
                    .background(Color.Black.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            )

            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 20.dp, end = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "关闭",
                    tint = Color.White
                )
            }
        }
    }
}

private fun createCameraImageUri(context: android.content.Context): Uri {
    val dir = File(context.cacheDir, "camera")
    if (!dir.exists()) dir.mkdirs()
    val file = File(dir, "item_${System.currentTimeMillis()}.jpg")
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
}


@Composable
fun AddButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(58.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = "保存",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}
