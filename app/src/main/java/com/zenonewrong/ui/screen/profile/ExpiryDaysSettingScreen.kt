package com.zenonewrong.ui.screen.profile

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zenonewrong.R
import com.zenonewrong.ui.theme.CardBlue
import com.zenonewrong.ui.theme.CardGreen
import com.zenonewrong.ui.theme.CardYellow
import com.zenonewrong.ui.theme.LineGrey
import com.zenonewrong.viewmodel.AppViewModel
import com.zenonewrong.viewmodel.ExpiryDaysViewModel


@Composable
fun ExpiryDaysSettingScreen() {
    val expiryDaysViewModel: ExpiryDaysViewModel = viewModel()
    val activity = LocalActivity.current as ComponentActivity
    val appViewModel: AppViewModel = viewModel(viewModelStoreOwner = activity)

    val expiryConfigs by expiryDaysViewModel.expiryConfigs.collectAsState()
    val showDialog by expiryDaysViewModel.showDialog.collectAsState()
    Scaffold(
        topBar = {
            TopBar(appViewModel)
        }) { inderPadding ->
        Column(
            modifier = Modifier
                .padding(inderPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors
                    (containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .padding()
                        .fillMaxWidth()
                ) {
                    expiryConfigs.forEachIndexed { index, config ->
                        val circleColor = when (config.tag) {
                            "yellow" -> CardYellow
                            "blue" -> CardBlue
                            "green" -> CardGreen
                            else -> CardYellow
                        }

                        ExpiryReminderCardWithCustomization(
                            days = config.days,
                            circleColor = circleColor,
                            onClick = { expiryDaysViewModel.showEditDialog(index) }
                        )

                        if (index < expiryConfigs.size - 1) {
                            HorizontalDivider(
                                color = LineGrey,
                                thickness = 0.dp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(.5.dp)
                                    .padding(start = 8.dp)
                            )
                        }
                    }


                }
            }
        }

        // 显示编辑对话框
        if (showDialog) {
            EditDaysDialog(
                viewModel = expiryDaysViewModel,
                onDismiss = { expiryDaysViewModel.hideDialog() },
                onConfirm = {
                    expiryDaysViewModel.saveConfig()
                }
            )
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(appViewModel: AppViewModel) {
    CenterAlignedTopAppBar(
        title = {
            Box(
                modifier = Modifier.fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.expiry_days_setting),
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
    )
}

@Composable
fun ExpiryReminderCardWithCustomization(
    days: Int = 3,
    backgroundColor: Color = Color.White,
    circleColor: Color = CardYellow,
    textColor: Color = Color.Black,
    circleSize: Int = 40,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .height(60.dp)
            .fillMaxWidth()
            .background(backgroundColor)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier.width(16.dp))
        // 左侧圆形图标
        Box(
            modifier = Modifier
                .size(circleSize.dp)
                .background(circleColor, shape = RoundedCornerShape((circleSize / 2).dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = days.toString(),
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = "${days}天内到期",
            color = textColor,
            fontSize = 16.sp
        )
    }
}

@Composable
fun EditDaysDialog(
    viewModel: ExpiryDaysViewModel,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val tempDays by viewModel.tempDays.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("设置到期天数", style = MaterialTheme.typography.titleMedium)
        },
        text = {
            Column {
                OutlinedTextField(
                    value = tempDays,
                    placeholder = {
                        Text("请输入1-999的数字")
                    },
                    onValueChange = {
                        viewModel.updateTempDays(it)
                    },
                    label = { Text("天数", style = MaterialTheme.typography.bodyMedium) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = tempDays.isNotEmpty() && tempDays.toIntOrNull() != null && tempDays.toIntOrNull()!! > 0 && tempDays.toIntOrNull()!! < 1000
            ) {
                Text("确定", style = MaterialTheme.typography.labelSmall)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消", style = MaterialTheme.typography.labelSmall)
            }
        }
    )
}
