package com.cospose.gallery.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class PresetColor(
    val name: String,
    val color: Color,
    val argb: Int
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ColorPickerSheet(
    selectedColor: Int?,
    onColorSelected: (Int?) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    val presetColors = listOf(
        PresetColor("红色", Color(0xFFE53935), 0xFFE53935.toInt()),
        PresetColor("橙色", Color(0xFFFF9800), 0xFFFF9800.toInt()),
        PresetColor("黄色", Color(0xFFFFEB3B), 0xFFFFEB3B.toInt()),
        PresetColor("绿色", Color(0xFF4CAF50), 0xFF4CAF50.toInt()),
        PresetColor("青色", Color(0xFF00BCD4), 0xFF00BCD4.toInt()),
        PresetColor("蓝色", Color(0xFF2196F3), 0xFF2196F3.toInt()),
        PresetColor("紫色", Color(0xFF9C27B0), 0xFF9C27B0.toInt()),
        PresetColor("粉色", Color(0xFFE91E63), 0xFFE91E63.toInt()),
        PresetColor("棕色", Color(0xFF795548), 0xFF795548.toInt()),
        PresetColor("灰色", Color(0xFF9E9E9E), 0xFF9E9E9E.toInt()),
        PresetColor("黑色", Color(0xFF212121), 0xFF212121.toInt()),
        PresetColor("白色", Color(0xFFFAFAFA), 0xFFFAFAFA.toInt()),
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
                "按颜色筛选",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "选择主色调",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(12.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                presetColors.forEach { preset ->
                    val isSelected = selectedColor == preset.argb
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(preset.color)
                            .border(
                                width = if (isSelected) 3.dp else 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.outline,
                                shape = CircleShape
                            )
                            .clickable {
                                onColorSelected(if (isSelected) null else preset.argb)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Text(
                                "✓",
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = { onColorSelected(null) },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("清除颜色筛选")
            }
        }
    }
}
