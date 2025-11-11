package com.zenonewrong.ui.screen.store

import android.content.Context
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zenonewrong.ui.screen.components.Topbar
import com.zenonewrong.viewmodel.AppViewModel
import com.zenonewrong.viewmodel.DataImportViewModel

@Composable
fun DataImportScreen() {
    val activity = LocalActivity.current as ComponentActivity
    val appViewModel: AppViewModel = viewModel(viewModelStoreOwner = activity)
    val dataImportViewModel: DataImportViewModel = viewModel(viewModelStoreOwner = activity)

    val isImporting by dataImportViewModel.isImporting.collectAsState()
    val showWarningDialog by dataImportViewModel.showWarningDialog.collectAsState()
    val importResult by dataImportViewModel.importResult.collectAsState()


    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            dataImportViewModel.selectFileUri(it)
            dataImportViewModel.showImportWarning()
        }
    }

    Scaffold(
        topBar = {
            Topbar(appViewModel, "导入")
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "警告：导入数据将会覆盖现有的所有数据！",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Red,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            Button(
                onClick = {
                    dataImportViewModel.setSelectedFileType(0)
                    filePickerLauncher.launch("*/*")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isImporting) "正在导入..." else "请选择物品CSV文件",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    dataImportViewModel.setSelectedFileType(1)
                    filePickerLauncher.launch("*/*")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                     text = if (isImporting) "正在导入..." else "请选择分类CSV文件",
                     style = MaterialTheme.typography.labelMedium,
                     color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            importResult?.let { result ->
                Text(
                    text = result,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }

    // 警告对话框
    if (showWarningDialog) {
        AlertDialog(
            onDismissRequest = {
                dataImportViewModel.hideWarningDialog()
            },
            title = {
                Text("确认导入", style = MaterialTheme.typography.titleMedium)
            },
            text = {
                Column {
                    Text("您确定要导入这个csv文件吗？", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "这将会删除所有现有的数据，并用csv文件中的数据替换它们。",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        dataImportViewModel.importCsvDataFormUri()
                    }
                ) {
                    Text("确认导入", style = MaterialTheme.typography.labelSmall)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        dataImportViewModel.hideWarningDialog()
                    }
                ) {
                    Text("取消", style = MaterialTheme.typography.labelSmall)
                }
            }
        )
    }
}

