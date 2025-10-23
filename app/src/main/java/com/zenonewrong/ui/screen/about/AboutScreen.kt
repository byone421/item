package com.zenonewrong.ui.screen.about

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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zenonewrong.R
import com.zenonewrong.Screen
import com.zenonewrong.ui.screen.components.Topbar
import com.zenonewrong.ui.screen.profile.SettingItem
import com.zenonewrong.ui.theme.LineGrey
import com.zenonewrong.ui.theme.TextGrey
import com.zenonewrong.viewmodel.AppViewModel


@Composable
fun AboutScreen() {
    val appViewModel: AppViewModel =
        viewModel(viewModelStoreOwner = LocalActivity.current as ComponentActivity)
    Scaffold(
        topBar = {
            Topbar(appViewModel,"关于软件")
        },
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier

                    .fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors
                    (containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Text(modifier = Modifier
                    .padding(16.dp),text = "物品记是一款使用Jetpack Compose开发的单Activity的小工具。所有记录都保存在手机本地，不会用于网络传输。欢迎大家使用。若需了解更多信息，请前往开源地址",
                    fontFamily = FontFamily.Default,
                    fontSize = 17.sp,
                    lineHeight = 17.sp,
                    letterSpacing = 0.3.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Card(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors
                    (containerColor = MaterialTheme.colorScheme.surface)

            ) {
                AboutItem(title = "开源地址", onClick = {}, showHorizontalDivider = false)
            }
            Spacer(modifier = Modifier.weight(1.0f))
            Box(modifier = Modifier.fillMaxWidth().padding(8.dp),
                contentAlignment = Alignment.Center){
                Text(text = "当前版本：V1.0.0",
                    fontFamily = FontFamily.Default,
                    fontSize = 16.sp,
                    lineHeight = 16.sp,
                    letterSpacing = 0.3.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }

}
@Composable
fun AboutItem(
    title: String,
    showHorizontalDivider: Boolean = true,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier.height(54.dp)
            .clickable{
                onClick()
            }
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(modifier= Modifier.widthIn(8.dp,8.dp))
        Text(title, style = MaterialTheme.typography.labelMedium)
        Spacer(modifier= Modifier.weight(1.0f))
        Icon(
            modifier = Modifier.size(22.dp), imageVector =
                Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = TextGrey
        )
    }
    if(showHorizontalDivider){
        HorizontalDivider(
            color = LineGrey,
            thickness = 0.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp)
                .height(.5.dp)
        )
    }
}