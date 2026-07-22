package com.zenonewrong.ui.screen.home

import androidx.compose.foundation.BorderStroke
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
import com.zenonewrong.bean.StatusCard
import com.zenonewrong.ui.theme.LineGrey
import com.zenonewrong.ui.theme.TextGrey

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StatusOverviewGrid(
    statusCards: List<StatusCard>,
    selectedDays: String? = null,
    onCardClick: (StatusCard) -> Unit = {}
) {
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 14.dp),
        maxItemsInEachRow = 2,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        statusCards.forEach {
            StatusCardItem(
                statusCard = it,
                selected = selectedDays == it.days,
                modifier = Modifier.weight(1f, fill = true),
                onItemClick = {
                    onCardClick(it)
                }
            )
        }
    }
}

@Composable
fun StatusCardItem(
    statusCard: StatusCard,
    selected: Boolean = false,
    modifier: Modifier = Modifier,
    onItemClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .height(62.dp)
            .clickable { onItemClick() },
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, LineGrey),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primary else Color(0xFFFFFAFD)
        )
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 10.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(if (selected) Color.White else statusCard.color, CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = statusCard.title,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (selected) Color.White else TextGrey,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "${statusCard.count}",
                    style = MaterialTheme.typography.titleLarge,
                    color = if (selected) Color.White else Color(0xFF2D2930)
                )
            }
        }
    }
}
