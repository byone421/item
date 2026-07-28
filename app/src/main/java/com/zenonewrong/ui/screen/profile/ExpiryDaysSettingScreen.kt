package com.zenonewrong.ui.screen.profile

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zenonewrong.R
import com.zenonewrong.notification.ExpiryNotificationScheduler
import com.zenonewrong.ui.screen.components.Topbar
import com.zenonewrong.ui.theme.CardBlue
import com.zenonewrong.ui.theme.CardGreen
import com.zenonewrong.ui.theme.CardYellow
import com.zenonewrong.ui.theme.BGGrey
import com.zenonewrong.ui.theme.LineGrey
import com.zenonewrong.ui.theme.TextGrey
import com.zenonewrong.viewmodel.AppViewModel
import com.zenonewrong.viewmodel.ExpiryDaysViewModel


@Composable
fun ExpiryDaysSettingScreen() {
    val expiryDaysViewModel: ExpiryDaysViewModel = viewModel()
    val activity = LocalActivity.current as ComponentActivity
    val context = LocalContext.current
    val appViewModel: AppViewModel = viewModel(viewModelStoreOwner = activity)

    val expiryConfigs by expiryDaysViewModel.expiryConfigs.collectAsState()
    val showDialog by expiryDaysViewModel.showDialog.collectAsState()
    var enabledTags by remember {
        mutableStateOf(ExpiryNotificationScheduler.enabledTags(context))
    }
    var pendingTag by remember { mutableStateOf<String?>(null) }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        pendingTag?.let { tag ->
            if (granted) {
                enabledTags = enabledTags + tag
                ExpiryNotificationScheduler.setEnabled(context, tag, true)
            }
        }
        pendingTag = null
    }

    Scaffold(
        containerColor = BGGrey,
        topBar = {
            Topbar(appViewModel, stringResource(R.string.expiry_days_setting))
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 18.dp, vertical = 16.dp)
        ) {
            Text(
                text = "通知",
                color = TextGrey,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "到期通知 · 每天 09:00",
                color = Color(0xFF2D2930),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(22.dp))
            Text(
                text = "提醒区间",
                color = TextGrey,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(10.dp))

            expiryConfigs.forEachIndexed { index, config ->
                val accentColor = when (config.tag) {
                    "yellow" -> CardYellow
                    "blue" -> CardBlue
                    "green" -> CardGreen
                    else -> CardYellow
                }

                val title = when (config.tag) {
                    "yellow" -> "近期提醒"
                    "blue" -> "中期提醒"
                    "green" -> "长期提醒"
                    else -> "到期提醒"
                }

                ExpiryReminderCardWithCustomization(
                    title = title,
                    days = config.days,
                    accentColor = accentColor,
                    enabled = config.tag in enabledTags,
                    onEnabledChange = { enabled ->
                        if (!enabled) {
                            enabledTags = enabledTags - config.tag
                            ExpiryNotificationScheduler.setEnabled(context, config.tag, false)
                        } else if (
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                            ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.POST_NOTIFICATIONS
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            pendingTag = config.tag
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            enabledTags = enabledTags + config.tag
                            ExpiryNotificationScheduler.setEnabled(context, config.tag, true)
                        }
                    },
                    onClick = { expiryDaysViewModel.showEditDialog(index) }
                )

                if (index < expiryConfigs.lastIndex) {
                    Spacer(modifier = Modifier.height(10.dp))
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

@Composable
fun ExpiryReminderCardWithCustomization(
    title: String,
    days: Int = 3,
    accentColor: Color = CardYellow,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onEnabledChange: (Boolean) -> Unit,
    onClick: () -> Unit = {}
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(74.dp),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, LineGrey),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFAFD))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(accentColor, CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                color = TextGrey,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = days.toString(),
                color = accentColor,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "天",
                color = TextGrey,
                fontSize = 13.sp
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "编辑$title",
                tint = TextGrey,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Switch(
                checked = enabled,
                onCheckedChange = onEnabledChange
            )
        }
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
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
