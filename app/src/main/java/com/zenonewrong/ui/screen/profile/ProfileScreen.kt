package com.zenonewrong.ui.screen.profile

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zenonewrong.Screen
import com.zenonewrong.ui.screen.components.Topbar
import com.zenonewrong.ui.theme.LineGrey
import com.zenonewrong.ui.theme.TextGrey
import com.zenonewrong.viewmodel.AppViewModel


@Composable
fun ProfileScreen() {
    val appViewModel: AppViewModel =
        viewModel(viewModelStoreOwner = LocalActivity.current as ComponentActivity)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F0F5))
            .verticalScroll(rememberScrollState())
            .padding(start = 18.dp, end = 18.dp, bottom = 20.dp)
    ) {
        Topbar(appViewModel, "我的",showBack = false)
        Text(
            text = "设置",
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TextGrey,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            border = BorderStroke(1.dp, LineGrey),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFAFD))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                SettingItem(title = "过期天数设置", onClick = {
                    appViewModel.navigateTo(Screen.Profile.route, Screen.ExpiryDaysSetting.route)
                })
//                SettingItem(title = "分类管理", showHorizontalDivider = false, onClick = {
//                    appViewModel.navigateTo(Screen.Profile.route, Screen.Classify.route)
//                })
            }
        }
        Spacer(modifier = Modifier.height(22.dp))
        Text(
            text = "转存数据",
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TextGrey,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            border = BorderStroke(1.dp, LineGrey),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFAFD))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                SettingItem(title = "导出", onClick = {
                    appViewModel.navigateTo(Screen.Profile.route, Screen.DateStore.route)
                })
                SettingItem(title = "导入", showHorizontalDivider = false, onClick = {
                    appViewModel.navigateTo(Screen.Profile.route, Screen.DataImport.route)
                })
            }
        }
        Spacer(modifier = Modifier.height(22.dp))
        Text(
            text = "其他",
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TextGrey,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            border = BorderStroke(1.dp, LineGrey),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFAFD))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                SettingItem(showHorizontalDivider = false,title = "关于软件", onClick = {
                    appViewModel.navigateTo(Screen.Profile.route, Screen.About.route)
                })

            }
        }
    }
}
