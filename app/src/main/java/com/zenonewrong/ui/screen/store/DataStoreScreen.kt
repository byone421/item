package com.zenonewrong.ui.screen.store

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zenonewrong.ui.screen.components.Topbar
import com.zenonewrong.viewmodel.AppViewModel
import com.zenonewrong.viewmodel.DataStoreViewModel

@Composable
fun DataStoreScreen() {
    val activity = LocalActivity.current as ComponentActivity
    val appViewModel: AppViewModel = viewModel(viewModelStoreOwner = activity)
    val dataStoreViewModel: DataStoreViewModel = viewModel(viewModelStoreOwner = activity)

    val isExporting by dataStoreViewModel.isExporting.collectAsState()
    val exportResult by dataStoreViewModel.exportResult.collectAsState()
    Scaffold(
        topBar = {
            Topbar(appViewModel,"导出")
        }) { inderPadding ->
        Column(
            modifier = Modifier
                .padding(inderPadding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = dataStoreViewModel.getExportDirectoryHint(),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 32.dp)
            )
            Button(
                onClick = {
                    dataStoreViewModel.setSelectedFileType(0)
                    dataStoreViewModel.exportToCsv()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isExporting
            ) {
                Text(if (isExporting) "正在导出物品..." else "导出物品数据到csv",  style = MaterialTheme.typography.labelMedium,
                    color = Color.White)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    dataStoreViewModel.setSelectedFileType(1)
                    dataStoreViewModel.exportToCsv()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isExporting
            ) {
                Text(if (isExporting) "正在导出分类..." else "导出分类数据到csv",  style = MaterialTheme.typography.labelMedium,
                    color = Color.White)
            }
            Spacer(modifier = Modifier.height(16.dp))
            exportResult?.let { result ->
                Text(
                    text = result,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}