package com.cospose.gallery.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.cospose.gallery.data.db.entity.TagEntity

/**
 * Category color mapping for visual distinction
 */
fun categoryColor(category: String): Color {
    return when (category.uppercase()) {
        "ACTION" -> Color(0xFF4CAF50)      // Green
        "CHARACTER" -> Color(0xFF2196F3)   // Blue
        "ANIME" -> Color(0xFF9C27B0)       // Purple
        "PHOTOGRAPHER" -> Color(0xFFFF9800) // Orange
        "COSPLAYER" -> Color(0xFFE91E63)   // Pink
        "OTHER" -> Color(0xFF607D8B)       // Blue Grey
        else -> Color(0xFF9E9E9E)          // Grey
    }
}

@Composable
fun TagGroupChip(
    tag: TagEntity,
    onClick: () -> Unit = {},
    onRemove: (() -> Unit)? = null,
    showCategory: Boolean = true,
    modifier: Modifier = Modifier
) {
    val categoryColor = categoryColor(tag.category)

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(categoryColor.copy(alpha = 0.15f))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Category indicator dot
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(categoryColor)
        )

        Spacer(modifier = Modifier.width(6.dp))

        // Tag name
        Text(
            text = if (showCategory) "${categoryLabel(tag.category)}·${tag.name}" else tag.name,
            style = MaterialTheme.typography.labelMedium,
            color = categoryColor
        )

        // Remove button
        if (onRemove != null) {
            Spacer(modifier = Modifier.width(4.dp))
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(16.dp)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "移除",
                    tint = categoryColor,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}

@Composable
fun TagGroupHeader(
    category: String,
    count: Int,
    modifier: Modifier = Modifier
) {
    val color = categoryColor(category)

    Row(
        modifier = modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "${categoryLabel(category)} ($count)",
            style = MaterialTheme.typography.titleSmall,
            color = color
        )
    }
}
