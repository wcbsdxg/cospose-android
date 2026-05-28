package com.cospose.gallery.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.cospose.gallery.data.db.entity.AnnotationEntity

@Composable
fun AnnotationOverlay(
    annotations: List<AnnotationEntity>,
    onAnnotationClick: (AnnotationEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        annotations.forEachIndexed { index, annotation ->
            if (annotation.xRatio != null && annotation.yRatio != null) {
                AnnotationPin(
                    index = index + 1,
                    onClick = { onAnnotationClick(annotation) },
                    modifier = Modifier
                        .offset(
                            x = (annotation.xRatio * 100).dp,
                            y = (annotation.yRatio * 100).dp
                        )
                )
            }
        }
    }
}

@Composable
private fun AnnotationPin(
    index: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.8f))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "$index",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White
        )
    }
}
