package com.cospose.gallery.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cospose.gallery.ui.theme.Purple100
import com.cospose.gallery.ui.theme.Purple500

@Composable
fun TagChip(
    name: String,
    category: String? = null,
    confidence: Float? = null,
    isAi: Boolean = false,
    onRemove: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val bgColor = if (isAi) Purple100 else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (isAi) Purple500 else MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = modifier
            .background(bgColor, RoundedCornerShape(16.dp))
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (category != null) {
            Text(
                text = "${categoryLabel(category)}·",
                style = MaterialTheme.typography.labelSmall,
                color = textColor.copy(alpha = 0.6f)
            )
        }
        Text(
            text = name,
            style = MaterialTheme.typography.labelMedium,
            color = textColor
        )
        if (confidence != null) {
            Text(
                text = " ${(confidence * 100).toInt()}%",
                style = MaterialTheme.typography.labelSmall,
                color = textColor.copy(alpha = 0.6f)
            )
        }
        if (onRemove != null) {
            Icon(
                Icons.Filled.Close,
                contentDescription = "移除",
                tint = textColor.copy(alpha = 0.6f),
                modifier = Modifier
                    .size(14.dp)
                    .padding(start = 2.dp)
                    .clickable(onClick = onRemove)
            )
        }
    }
}

fun categoryLabel(category: String): String = when (category) {
    "CHARACTER" -> "角色"
    "ANIME" -> "作品"
    "ACTION" -> "动作"
    "PHOTOGRAPHER" -> "摄影"
    "COSPLAYER" -> "Coser"
    "OTHER" -> "场景"
    else -> category
}
