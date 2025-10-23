package com.zenonewrong.ui.screen.iteminfo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zenonewrong.viewmodel.ItemInfoViewModel

@Composable
fun TimeUnitSelector(vm: ItemInfoViewModel) {

    val list = listOf("天", "周", "月", "年")

    // 对话框
    AlertDialog(
        onDismissRequest = { vm.dismissStorageUnit() },
        confirmButton = {},
        title = {
            Text(
                text = "选择时间单位",
                style = MaterialTheme.typography.titleMedium
            )
        },
        text = {
            Column {
                for (item in list) {
                    TextButton(
                        onClick = {
                            vm.onStorageSelect(item)

                        }, modifier = Modifier
                            .height(40.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = item,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        },)
}