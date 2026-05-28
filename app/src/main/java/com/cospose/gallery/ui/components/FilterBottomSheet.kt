package com.cospose.gallery.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cospose.gallery.ui.home.AspectRatio
import com.cospose.gallery.ui.home.FilterState
import com.cospose.gallery.ui.home.MimeTypeFilter
import com.cospose.gallery.ui.home.TimeRange

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterBottomSheet(
    currentState: FilterState,
    onApply: (FilterState) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var minRating by remember { mutableFloatStateOf(currentState.minRating ?: 0f) }
    var maxRating by remember { mutableFloatStateOf(currentState.maxRating ?: 5f) }
    var selectedAspectRatio by remember { mutableStateOf(currentState.aspectRatio ?: AspectRatio.ANY) }
    var selectedTimeRange by remember {
        mutableStateOf(
            TimeRange.entries.find { it.days == currentState.sinceTimestamp?.let { t ->
                val daysAgo = (System.currentTimeMillis() - t) / (24 * 60 * 60 * 1000)
                daysAgo
            } } ?: TimeRange.ANY
        )
    }
    var selectedMimeType by remember {
        mutableStateOf(
            MimeTypeFilter.entries.find { it.mimeType == currentState.mimeType } ?: MimeTypeFilter.ALL
        )
    }
    var selectedColor by remember { mutableStateOf(currentState.dominantColor) }

    val presetColors = listOf(
        Triple("红色", Color(0xFFE53935), 0xFFE53935.toInt()),
        Triple("橙色", Color(0xFFFF9800), 0xFFFF9800.toInt()),
        Triple("黄色", Color(0xFFFFEB3B), 0xFFFFEB3B.toInt()),
        Triple("绿色", Color(0xFF4CAF50), 0xFF4CAF50.toInt()),
        Triple("青色", Color(0xFF00BCD4), 0xFF00BCD4.toInt()),
        Triple("蓝色", Color(0xFF2196F3), 0xFF2196F3.toInt()),
        Triple("紫色", Color(0xFF9C27B0), 0xFF9C27B0.toInt()),
        Triple("粉色", Color(0xFFE91E63), 0xFFE91E63.toInt()),
        Triple("棕色", Color(0xFF795548), 0xFF795548.toInt()),
        Triple("灰色", Color(0xFF9E9E9E), 0xFF9E9E9E.toInt()),
        Triple("黑色", Color(0xFF212121), 0xFF212121.toInt()),
        Triple("白色", Color(0xFFFAFAFA), 0xFFFAFAFA.toInt()),
    )

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
                "筛选条件",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Rating range
            Text(
                "评分范围",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "${"%.1f".format(minRating)} 星",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(8.dp))
                Slider(
                    value = minRating,
                    onValueChange = { minRating = it },
                    valueRange = 0f..5f,
                    steps = 9,
                    modifier = Modifier.weight(1f),
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "${"%.1f".format(maxRating)} 星",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(8.dp))
                Slider(
                    value = maxRating,
                    onValueChange = { maxRating = it },
                    valueRange = 0f..5f,
                    steps = 9,
                    modifier = Modifier.weight(1f),
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Aspect ratio
            Text(
                "图片比例",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AspectRatio.entries.forEach { ratio ->
                    FilterChip(
                        selected = selectedAspectRatio == ratio,
                        onClick = { selectedAspectRatio = ratio },
                        label = { Text(ratio.label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Time range
            Text(
                "上传时间",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TimeRange.entries.forEach { range ->
                    FilterChip(
                        selected = selectedTimeRange == range,
                        onClick = { selectedTimeRange = range },
                        label = { Text(range.label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // File type
            Text(
                "文件格式",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MimeTypeFilter.entries.forEach { type ->
                    FilterChip(
                        selected = selectedMimeType == type,
                        onClick = { selectedMimeType = type },
                        label = { Text(type.label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Color filter
            Text(
                "主色调",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                presetColors.forEach { (name, color, argb) ->
                    val isSelected = selectedColor == argb
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(color)
                            .border(
                                width = if (isSelected) 3.dp else 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.outline,
                                shape = CircleShape
                            )
                            .clickable {
                                selectedColor = if (isSelected) null else argb
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Text(
                                "✓",
                                color = Color.White,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = {
                        onApply(FilterState())
                    }
                ) {
                    Text("重置")
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(
                    onClick = {
                        val sinceTimestamp = selectedTimeRange.days?.let { days ->
                            System.currentTimeMillis() - days * 24 * 60 * 60 * 1000
                        }
                        onApply(
                            FilterState(
                                minRating = if (minRating > 0f) minRating else null,
                                maxRating = if (maxRating < 5f) maxRating else null,
                                aspectRatio = if (selectedAspectRatio != AspectRatio.ANY) selectedAspectRatio else null,
                                sinceTimestamp = sinceTimestamp,
                                mimeType = selectedMimeType.mimeType,
                                dominantColor = selectedColor
                            )
                        )
                    },
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.primary,
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 4.dp)
                ) {
                    Text(
                        "应用筛选",
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}
