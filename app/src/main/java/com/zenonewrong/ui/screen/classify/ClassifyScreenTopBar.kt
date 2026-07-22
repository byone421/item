package com.zenonewrong.ui.screen.classify

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.zenonewrong.R
import com.zenonewrong.Screen
import com.zenonewrong.ui.screen.components.Topbar
import com.zenonewrong.ui.screen.components.TopbarIconButton
import com.zenonewrong.viewmodel.AppViewModel
import com.zenonewrong.viewmodel.ClassifyViewModel

@Composable
fun ClassifyScreenTopBar(
    appViewModel: AppViewModel,
    classifyVM: ClassifyViewModel,
    showBack: Boolean = true
) {
    Topbar(
        appViewModel = appViewModel,
        showBack = false,
        title = stringResource(R.string.classify),
        onBack = {
            if (showBack) appViewModel.navigateBack()
            else appViewModel.currentTab = Screen.Home.route
        }
    ) {
        TopbarIconButton(
            onClick = {
                classifyVM.cleanState()
                classifyVM.toggleDialog(true)
            },
            imageVector = Icons.Default.Add,
            contentDescription = "添加分类"
        )
    }
}
