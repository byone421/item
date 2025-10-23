package com.zenonewrong.ui.screen.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CopyAll
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Details
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import com.linversion.swipe.SwipeAction
import com.linversion.swipe.SwipeableActionsBox
import com.zenonewrong.entity.Classify
import com.zenonewrong.entity.ItemInfo
import com.zenonewrong.ui.theme.Purple40

/**
 * 创建物品列表项的滑动操作
 */
@Composable
fun createItemSwipeActions(
    itemInfo: ItemInfo,
    onDelete: (ItemInfo) -> Unit,
    onEdit: (ItemInfo) -> Unit,
    onCopy: (ItemInfo) -> Unit
): List<SwipeAction> {
    return listOf(
        SwipeAction(
            icon = tintedVectorPainter(Icons.Default.Delete, Color.White),
            background = Color.Red,
            onClick = { onDelete(itemInfo) }
        ),
        SwipeAction(
            icon = tintedVectorPainter(Icons.Default.Edit, Color.White),
            background = Purple40,
            onClick = { onEdit(itemInfo) }
        ),
        SwipeAction(
            icon = tintedVectorPainter(Icons.Default.CopyAll, Color.White),
            background = Color.Blue,
            onClick = { onCopy(itemInfo) }
        )
    )
}

/**
 * 带滑动操作的物品容器
 */
@Composable
fun SwipeableItemContainer(
    itemInfo: ItemInfo,
    onDelete: (ItemInfo) -> Unit,
    onEdit: (ItemInfo) -> Unit,
    onCopy: (ItemInfo) -> Unit,
    content: @Composable () -> Unit
) {
    val swipeActions = createItemSwipeActions(itemInfo, onDelete, onEdit, onCopy)

    SwipeableActionsBox(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        swipeThreshold = 50.dp,
        endActions = swipeActions,
    ) {
        content()
    }
}

/**
 * 创建分类列表项的滑动操作
 */
@Composable
fun createClassifySwipeActions(
    classify: Classify,
    onDelete: (Classify) -> Unit,
    onEdit: (Classify) -> Unit,
    onDetail: (Classify) -> Unit
): List<SwipeAction> {
    return listOf(
        SwipeAction(
            icon = tintedVectorPainter(Icons.Default.Delete, Color.White),
            background = Color.Red,
            onClick = { onDelete(classify) }
        ),
        SwipeAction(
            icon = tintedVectorPainter(Icons.Default.Edit, Color.White),
            background = Purple40,
            onClick = { onEdit(classify) }
        ),
        SwipeAction(
            icon = tintedVectorPainter(Icons.Default.Details, Color.White),
            background = Color.Blue,
            onClick = { onDetail(classify) }
        )
    )
}

/**
 * 带滑动操作的分类容器
 */
@Composable
fun SwipeableClassifyContainer(
    classify: Classify,
    onDelete: (Classify) -> Unit,
    onEdit: (Classify) -> Unit,
    onDetail: (Classify) -> Unit,
    content: @Composable () -> Unit
) {
    val swipeActions = createClassifySwipeActions(classify, onDelete, onEdit, onDetail)

    SwipeableActionsBox(
        modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 4.dp),
        swipeThreshold = 50.dp,
        endActions = swipeActions,
    ) {
        content()
    }
}

/**
 * 创建带颜色的矢量图标绘制器
 */
@Composable
fun tintedVectorPainter(
    image: ImageVector,
    tint: Color
): Painter {
    val basePainter = rememberVectorPainter(image)
    return remember(basePainter, tint) {
        object : Painter() {
            override val intrinsicSize = basePainter.intrinsicSize
            override fun DrawScope.onDraw() {
                with(basePainter) {
                    draw(size = size, colorFilter = ColorFilter.tint(tint))
                }
            }
        }
    }
}