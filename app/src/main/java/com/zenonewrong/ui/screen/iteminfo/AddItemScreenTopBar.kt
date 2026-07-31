package com.zenonewrong.ui.screen.iteminfo

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import com.zenonewrong.ui.screen.components.Topbar
import com.zenonewrong.ui.screen.components.TopbarIconButton
import com.zenonewrong.viewmodel.AppViewModel
import com.zenonewrong.viewmodel.ItemInfoViewModel

@Composable
fun AddItemScreenTopBar(appViewModel: AppViewModel, itemInfoVM: ItemInfoViewModel) {
    Topbar(
        appViewModel = appViewModel,
        title = "记录物品",
        onBack = {
            itemInfoVM.clearForm()
            appViewModel.navigateBack()
        }
    )
//    {
//        TopbarIconButton(
//            onClick = {},
//            imageVector = Icons.Default.MoreVert,
//            contentDescription = "更多"
//        )
//    }
}
