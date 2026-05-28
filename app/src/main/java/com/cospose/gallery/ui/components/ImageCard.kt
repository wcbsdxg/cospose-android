package com.cospose.gallery.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.cospose.gallery.data.db.entity.ImageEntity
import com.cospose.gallery.ui.theme.HeartRed
import com.cospose.gallery.ui.theme.StarYellow
import java.io.File

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageCard(
    image: ImageEntity,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    isSelected: Boolean = false,
    isSelectionMode: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            else
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column {
            // Image
            val imageFile = image.thumbnailPath?.let { File(it) }
            val aspectRatio = if (image.width > 0 && image.height > 0) {
                image.width.toFloat() / image.height.toFloat()
            } else {
                3f / 4f
            }

            Box {
                AsyncImage(
                    model = imageFile ?: image.filePath,
                    contentDescription = image.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(aspectRatio)
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                    contentScale = ContentScale.Crop,
                )

                // Selection checkbox overlay
                if (isSelectionMode) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp)
                    ) {
                        Icon(
                            if (isSelected) Icons.Filled.CheckCircle else Icons.Filled.CheckCircle,
                            contentDescription = if (isSelected) "已选中" else "未选中",
                            tint = if (isSelected) MaterialTheme.colorScheme.primary
                            else Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                // Stats overlay at bottom
                if (image.likesCount > 0 || image.ratingAvg > 0) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(6.dp)
                            .background(
                                Color.Black.copy(alpha = 0.5f),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (image.ratingAvg > 0) {
                            Icon(
                                Icons.Filled.Star,
                                contentDescription = null,
                                tint = StarYellow,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                String.format("%.1f", image.ratingAvg),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        if (image.likesCount > 0) {
                            Icon(
                                Icons.Filled.Favorite,
                                contentDescription = null,
                                tint = HeartRed,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                "${image.likesCount}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // Title
            Text(
                text = image.title,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}
