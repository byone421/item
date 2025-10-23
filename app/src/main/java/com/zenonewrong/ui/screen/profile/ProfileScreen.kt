package com.zenonewrong.ui.screen.profile

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zenonewrong.Screen
import com.zenonewrong.ui.theme.TextGrey
import com.zenonewrong.viewmodel.AppViewModel


@Composable
fun ProfileScreen() {
    val appViewModel: AppViewModel =
        viewModel(viewModelStoreOwner = LocalActivity.current as ComponentActivity)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "设置",
            style = MaterialTheme.typography.labelMedium,
            color = TextGrey,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors
                (containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                SettingItem(title = "过期天数设置", onClick = {
                    appViewModel.navigateTo(Screen.Profile.route, Screen.ExpiryDaysSetting.route)
                })
                SettingItem(title = "分类管理", showHorizontalDivider = false, onClick = {
                    appViewModel.navigateTo(Screen.Profile.route, Screen.Classify.route)
                })
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "转存数据",
            style = MaterialTheme.typography.labelMedium,
            color = TextGrey,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors
                (containerColor = MaterialTheme.colorScheme.surface)
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
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "其他",
            style = MaterialTheme.typography.labelMedium,
            color = TextGrey,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors
                (containerColor = MaterialTheme.colorScheme.surface)
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
