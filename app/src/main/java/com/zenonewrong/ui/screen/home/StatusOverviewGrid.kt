package com.zenonewrong.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Colors
import com.zenonewrong.bean.StatusCard

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StatusOverviewGrid(
    statusCards: List<StatusCard>,
    onCardClick: (StatusCard) -> Unit = {}
) {
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        maxItemsInEachRow = 2,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        statusCards.forEach {
            StatusCardItem(
                statusCard = it,
                modifier = Modifier.weight(1f, fill = true),
                onItemClick = {
                    if(it.count>0){
                        onCardClick(it)
                    }
                }
            )
        }
    }
}

@Composable
fun StatusCardItem(
    statusCard: StatusCard,
    modifier: Modifier = Modifier,
    onItemClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .height(110.dp)
            .clickable { onItemClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 彩色圆形图标
                Box(
                    modifier = Modifier
                        .size(45.dp)
                        .background(
                            color = statusCard.color,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // 这里可以放置图标，暂时用颜色块代替
                    Text(
                        text = statusCard.days,
                        style = MaterialTheme.typography.displayMedium
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // 数量显示
                Text(
                    text = "${statusCard.count}",
                    style = MaterialTheme.typography.displayLarge
                )
            }

            // 底部：状态名称
            Text(
                text = statusCard.title,
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.align(Alignment.Start)
            )
        }
    }
}
