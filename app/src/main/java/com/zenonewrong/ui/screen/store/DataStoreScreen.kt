package com.zenonewrong.ui.screen.store

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zenonewrong.R
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
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = dataStoreViewModel.getExportDirectoryHint(),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 32.dp)
            )
            Button(
                onClick = {
                    dataStoreViewModel.exportToExcel()
                },
                enabled = !isExporting
            ) {
                Text(if (isExporting) "正在导出..." else "导出数据到Excel",  style = MaterialTheme.typography.labelMedium,
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