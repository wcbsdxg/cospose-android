package com.cospose.gallery.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cospose.gallery.data.db.entity.ImageEntity
import com.cospose.gallery.util.ExifReader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetadataSheet(
    image: ImageEntity,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val exifDisplayMap = remember(image.filePath) {
        val exifData = ExifReader.readExif(image.filePath)
        exifData?.let { ExifReader.toDisplayMap(it) } ?: emptyMap()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                "图片详情",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // File info section
            Text(
                "文件信息",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))

            MetadataRow("标题", image.title)
            image.description?.let { MetadataRow("描述", it) }
            MetadataRow("分辨率", "${image.width} × ${image.height}")
            MetadataRow("文件格式", image.mimeType)
            MetadataRow("文件大小", ExifReader.formatFileSize(image.fileSize))
            MetadataRow("评分", String.format("%.1f / 5", image.ratingAvg))
            MetadataRow("点赞数", "${image.likesCount}")
            MetadataRow("评论数", "${image.commentsCount}")

            // EXIF data section
            if (exifDisplayMap.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "拍摄信息",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))

                exifDisplayMap.forEach { (key, value) ->
                    MetadataRow(key, value)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sync status
            Text(
                "同步状态",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))

            MetadataRow("同步状态", image.syncStatus)
            image.serverId?.let { MetadataRow("服务器ID", it) }
        }
    }
}

@Composable
private fun MetadataRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(100.dp)
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
    }
}
