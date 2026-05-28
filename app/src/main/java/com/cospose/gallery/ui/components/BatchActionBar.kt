package com.cospose.gallery.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BatchActionBar(
    selectedCount: Int,
    onSelectAll: () -> Unit,
    onClearSelection: () -> Unit,
    onBatchTag: () -> Unit,
    onBatchRate: () -> Unit,
    onBatchDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    BottomAppBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Close and count
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = onClearSelection) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "取消选择",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    "已选择 $selectedCount 项",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Right: Actions
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(onClick = onSelectAll) {
                    Icon(
                        Icons.Default.SelectAll,
                        contentDescription = "全选",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                IconButton(onClick = onBatchTag) {
                    Icon(
                        Icons.Default.Label,
                        contentDescription = "批量打标签",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                IconButton(onClick = onBatchRate) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = "批量评分",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                IconButton(onClick = onBatchDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "批量删除",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
