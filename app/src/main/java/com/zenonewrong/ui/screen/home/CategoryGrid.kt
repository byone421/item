package com.zenonewrong.ui.screen.home


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zenonewrong.entity.Classify

/**
 * 顶部标题栏
 */
@Composable
fun CategoryTopBar(
    categoryCount: Int,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top=0.dp, end = 16.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "我的分类（$categoryCount）",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

/**
 * 分类表格列表
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategoryGrid(
    categories: List<Classify>,
    onCategoryClick: (Classify) -> Unit = {}
) {

    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        maxItemsInEachRow = 2,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categories.forEach {
            CategoryCard(
                category = it,
                onClick = { onCategoryClick(it) },
                modifier = Modifier.weight(1f, fill = true)
            )
        }
    }
}


/**
 * 分类条目
 */
@Composable
fun CategoryCard(
    category: Classify,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp) // 固定高度
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 分类名称
            Text(
                text = category.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Black
            )
        }
    }
}

/**
 * 分类整体
 */
@Composable
fun CategoryView(
    categories: List<Classify> = emptyList(),
    onCategoryClick: (Classify) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .wrapContentHeight()
    ) {
        // 顶部标题栏
        CategoryTopBar(
            categoryCount = categories.size
        )
        // 分类网格
        CategoryGrid(
            categories = categories,
            onCategoryClick = onCategoryClick
        )
    }
}