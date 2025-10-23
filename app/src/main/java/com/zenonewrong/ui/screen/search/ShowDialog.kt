package com.zenonewrong.ui.screen.search

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import com.zenonewrong.entity.ItemInfo

@Composable
fun DeleteItemDialog(
    item: ItemInfo,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "确认删除",
                style = MaterialTheme.typography.titleMedium
            )
        },
        text = {
            Text(
                text = "确定要删除物品 \"${item.name}\" 吗？此操作不可撤销。",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier
            ) {
                Text(
                    text = "删除",
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(
                    text = "取消",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    )
}
@Composable
fun DeleteAllExpiredDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "确认删除所有过期物品",
                style = MaterialTheme.typography.titleMedium
            )
        },
        text = {
            Text(
                text = "确定要删除所有已过期的物品吗？此操作不可撤销，请谨慎操作。",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier
            ) {
                Text(
                    text = "删除全部",
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(
                    text = "取消",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    )
}