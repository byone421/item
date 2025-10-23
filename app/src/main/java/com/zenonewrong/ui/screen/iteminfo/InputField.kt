package com.zenonewrong.ui.screen.iteminfo

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.zenonewrong.ui.theme.LineGrey
import com.zenonewrong.ui.theme.TextGrey

@Composable
fun InputField(
    required: Boolean = false,
    label: String,
    text: String,
    showHorizontalDivider: Boolean = true,
    readOnly: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Unspecified,
    onValueChange: (String) -> Unit,
    onClick: () -> Unit = {},
    trailingContent: @Composable (() -> Unit)? = null,
) {

    // 将点击区域放在最上层
    BasicTextField(
        value = text,
        textStyle = MaterialTheme.typography.labelMedium,
        onValueChange = if (readOnly) { _ -> } else onValueChange,
        readOnly = readOnly,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        maxLines = 1,
        minLines = 1,

        decorationBox = { innerTextField ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxHeight()
            ) {
                Row(modifier = Modifier.width(70.dp)) {
                    if (required) {
                        Text(
                            modifier = Modifier.width(5.dp),
                            text = "*",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Red
                        )
                    } else {
                        Spacer(modifier = Modifier.width(5.dp))
                    }
                    Text(
                        text = label,
                        modifier = Modifier.width(100.dp),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
                Box(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    if (text.isEmpty()) {
                        Text(
                            text = "请输入${label}",
                            style = MaterialTheme.typography.labelMedium, color = TextGrey
                        )
                    }
                    if (readOnly) {
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable(
                                    onClick = onClick
                                ),
                            contentAlignment= Alignment.CenterStart
                        ){
                            innerTextField()
                        }
                    } else {
                        innerTextField()
                    }

                }
                trailingContent?.invoke()
            }
        },
        modifier = Modifier
            .background(Color.White)
            .height(52.dp)
            .fillMaxWidth()
    )


    if (showHorizontalDivider) {
        HorizontalDivider(
            color = LineGrey,
            thickness = 0.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(.5.dp)
                .padding(start = 8.dp)
        )
    }
}