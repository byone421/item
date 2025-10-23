package com.zenonewrong.ui.screen.iteminfo

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.zenonewrong.viewmodel.ItemInfoViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowDatePicker(vm: ItemInfoViewModel) {
    val dateFormatter = remember { DateTimeFormatter.ofPattern("yyyy-MM-dd") }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Instant.now().toEpochMilli()
    )
    DatePickerDialog(
        onDismissRequest = {
            vm.dismissDatePicker()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val selectedDate = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                            .format(dateFormatter)
                        vm.onDateSelected(selectedDate)
                    }
                    vm.dismissDatePicker()
                }
            ) {
                Text(
                    text = "确认",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        },
        dismissButton = {
            TextButton(onClick = { vm.dismissDatePicker() }) {
                Text(
                    text = "取消",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    ) {
        DatePicker(
            state = datePickerState,
        )
    }

}