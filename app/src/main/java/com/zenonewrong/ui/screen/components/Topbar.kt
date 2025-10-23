package com.zenonewrong.ui.screen.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zenonewrong.R
import com.zenonewrong.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Topbar(appViewModel: AppViewModel,title: String) {
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
            titleContentColor = Color.Black
        ),
        navigationIcon = {
            IconButton(onClick = { appViewModel.navigateBack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back)
                )
            }
        }
    )
}
