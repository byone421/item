package com.zenonewrong.ui.screen.about

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zenonewrong.ui.screen.components.Topbar
import com.zenonewrong.ui.screen.profile.SettingItem
import com.zenonewrong.ui.theme.BGGrey
import com.zenonewrong.ui.theme.LineGrey
import com.zenonewrong.ui.theme.TextGrey
import com.zenonewrong.viewmodel.AppViewModel

@Composable
fun AboutScreen() {
    val appViewModel: AppViewModel =
        viewModel(viewModelStoreOwner = LocalActivity.current as ComponentActivity)
    val context = LocalContext.current
    val versionName = context.packageManager
        .getPackageInfo(context.packageName, 0)
        .versionName
        .orEmpty()
    val openUrl: (String) -> Unit = { url ->
        context.startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
    }

    Scaffold(
        containerColor = BGGrey,
        topBar = { Topbar(appViewModel, "关于软件") }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BGGrey)
                .padding(innerPadding)
                .padding(start = 18.dp, end = 18.dp, bottom = 20.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, LineGrey),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFAFD))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "物品记",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF2D2930)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "一款使用 Jetpack Compose 开发的单 Activity 小工具。所有记录都保存在手机本地，不会用于网络传输。欢迎大家使用。",
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp,
                        color = TextGrey
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))
            Text(
                text = "相关链接",
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextGrey
            )
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, LineGrey),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFAFD))
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    SettingItem(title = "开源地址") {
                        openUrl("https://github.com/byone421/item")
                    }
                    SettingItem(title = "版本列表", showHorizontalDivider = false) {
                        openUrl("https://github.com/byone421/item/tags")
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "当前版本 V$versionName",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = TextGrey,
                textAlign = TextAlign.Center
            )
        }
    }
}
