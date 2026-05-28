package com.cospose.gallery.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cospose.gallery.data.ThemeMode
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val themeMode by viewModel.themeMode.collectAsState()
    val dynamicColor by viewModel.dynamicColor.collectAsState()
    val gridColumns by viewModel.gridColumns.collectAsState()
    val syncEnabled by viewModel.syncEnabled.collectAsState()
    val serverUrl by viewModel.serverUrl.collectAsState()
    val showTags by viewModel.showTags.collectAsState()
    val compressUpload by viewModel.compressUpload.collectAsState()

    var showStorageDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "设置",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Theme section
            SectionHeader("外观")
            SettingsItem(
                icon = Icons.Filled.DarkMode,
                title = "主题模式",
                subtitle = when (themeMode) {
                    ThemeMode.SYSTEM -> "跟随系统"
                    ThemeMode.LIGHT -> "浅色"
                    ThemeMode.DARK -> "深色"
                },
                trailing = {
                    SingleChoiceSegmentedButtonRow {
                        ThemeMode.entries.forEachIndexed { index, mode ->
                            SegmentedButton(
                                selected = themeMode == mode,
                                onClick = { viewModel.setThemeMode(mode) },
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = index,
                                    count = ThemeMode.entries.size
                                )
                            ) {
                                Text(
                                    when (mode) {
                                        ThemeMode.SYSTEM -> "系统"
                                        ThemeMode.LIGHT -> "浅色"
                                        ThemeMode.DARK -> "深色"
                                    }
                                )
                            }
                        }
                    }
                }
            )
            SettingsSwitchItem(
                icon = Icons.Filled.Palette,
                title = "动态颜色",
                subtitle = "使用 Material You 取色",
                checked = dynamicColor,
                onCheckedChange = { viewModel.setDynamicColor(it) }
            )
            SettingsSwitchItem(
                icon = Icons.Filled.GridView,
                title = "显示标签",
                subtitle = "在图片卡片上显示标签",
                checked = showTags,
                onCheckedChange = { viewModel.setShowTags(it) }
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            SectionHeader("网格")

            SettingsItem(
                icon = Icons.Filled.GridView,
                title = "网格列数",
                subtitle = "${gridColumns} 列",
                trailing = {
                    SingleChoiceSegmentedButtonRow {
                        listOf(2, 3, 4).forEachIndexed { index, cols ->
                            SegmentedButton(
                                selected = gridColumns == cols,
                                onClick = { viewModel.setGridColumns(cols) },
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = index,
                                    count = 3
                                )
                            ) {
                                Text("$cols")
                            }
                        }
                    }
                }
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            SectionHeader("同步")

            SettingsSwitchItem(
                icon = Icons.Filled.Sync,
                title = "服务器同步",
                subtitle = if (syncEnabled) "已开启" else "已关闭",
                checked = syncEnabled,
                onCheckedChange = { viewModel.setSyncEnabled(it) }
            )
            SettingsItem(
                icon = Icons.Filled.Language,
                title = "服务器地址",
                subtitle = serverUrl,
                onClick = { /* TODO: show edit dialog */ }
            )
            SettingsSwitchItem(
                icon = Icons.Filled.Save,
                title = "压缩上传",
                subtitle = "上传前压缩图片以节省流量",
                checked = compressUpload,
                onCheckedChange = { viewModel.setCompressUpload(it) }
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            SectionHeader("存储")

            SettingsItem(
                icon = Icons.Filled.Storage,
                title = "存储管理",
                subtitle = "本地图片和缓存",
                onClick = { showStorageDialog = true }
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            SectionHeader("关于")

            SettingsItem(
                icon = Icons.Filled.Info,
                title = "关于",
                subtitle = "CosPose Gallery v1.0.0",
                onClick = { }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // Storage dialog
    if (showStorageDialog) {
        StorageDialog(
            onDismiss = { showStorageDialog = false }
        )
    }
}

@Composable
private fun StorageDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val imagesDir = File(context.filesDir, "images")

    // Calculate storage info
    val storageInfo = remember {
        calculateStorageInfo(imagesDir)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "存储管理",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Storage usage card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            "存储使用",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Progress bar
                        LinearProgressIndicator(
                            progress = { (storageInfo.usedBytes.toFloat() / storageInfo.totalBytes).coerceIn(0f, 1f) },
                            modifier = Modifier.fillMaxWidth(),
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "已用: ${formatFileSize(storageInfo.usedBytes)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                "总计: ${formatFileSize(storageInfo.totalBytes)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Image counts
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            "图片统计",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        StorageStatRow("原图", storageInfo.originalCount, storageInfo.originalSize)
                        StorageStatRow("缩略图", storageInfo.thumbCount, storageInfo.thumbSize)
                        StorageStatRow("中等图", storageInfo.mediumCount, storageInfo.mediumSize)
                    }
                }

                // Clear cache button
                OutlinedButton(
                    onClick = {
                        // Clear cache (thumbnails and medium images)
                        imagesDir.listFiles()?.forEach { file ->
                            if (file.name.contains("_thumb") || file.name.contains("_medium")) {
                                file.delete()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("清除缓存")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("关闭")
            }
        }
    )
}

@Composable
private fun StorageStatRow(label: String, count: Int, size: Long) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            "$count 张 (${formatFileSize(size)})",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private data class StorageInfo(
    val totalBytes: Long,
    val usedBytes: Long,
    val originalCount: Int,
    val originalSize: Long,
    val thumbCount: Int,
    val thumbSize: Long,
    val mediumCount: Int,
    val mediumSize: Long
)

private fun calculateStorageInfo(imagesDir: File): StorageInfo {
    if (!imagesDir.exists()) {
        return StorageInfo(
            totalBytes = 1024 * 1024 * 1024, // 1GB default
            usedBytes = 0,
            originalCount = 0,
            originalSize = 0,
            thumbCount = 0,
            thumbSize = 0,
            mediumCount = 0,
            mediumSize = 0
        )
    }

    var originalCount = 0
    var originalSize = 0L
    var thumbCount = 0
    var thumbSize = 0L
    var mediumCount = 0
    var mediumSize = 0L

    imagesDir.listFiles()?.forEach { file ->
        val size = file.length()
        when {
            file.name.contains("_original") -> {
                originalCount++
                originalSize += size
            }
            file.name.contains("_thumb") -> {
                thumbCount++
                thumbSize += size
            }
            file.name.contains("_medium") -> {
                mediumCount++
                mediumSize += size
            }
        }
    }

    val usedBytes = originalSize + thumbSize + mediumSize
    val totalBytes = maxOf(usedBytes * 2, 1024 * 1024 * 1024) // At least 1GB or 2x used

    return StorageInfo(
        totalBytes = totalBytes,
        usedBytes = usedBytes,
        originalCount = originalCount,
        originalSize = originalSize,
        thumbCount = thumbCount,
        thumbSize = thumbSize,
        mediumCount = mediumCount,
        mediumSize = mediumSize
    )
}

private fun formatFileSize(bytes: Long): String {
    return when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "${bytes / 1024} KB"
        bytes < 1024 * 1024 * 1024 -> "${"%.1f".format(bytes / (1024.0 * 1024.0))} MB"
        else -> "${"%.2f".format(bytes / (1024.0 * 1024.0 * 1024.0))} GB"
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
    )
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    trailing: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (trailing != null) {
            trailing()
        }
    }
    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
}

@Composable
private fun SettingsSwitchItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
}
