package com.cospose.gallery.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.cospose.gallery.ui.components.BatchActionBar
import com.cospose.gallery.ui.components.BatchTagDialog
import com.cospose.gallery.ui.components.FilterBottomSheet
import com.cospose.gallery.ui.components.ImageCard
import com.cospose.gallery.ui.components.SelectionState
import com.cospose.gallery.ui.components.StarRating
import com.cospose.gallery.ui.components.categoryLabel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    onImageClick: (String) -> Unit,
    onUploadClick: () -> Unit,
    onPreviewClick: ((startIndex: Int, imageIds: String) -> Unit)? = null,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val sortMode by viewModel.sortMode.collectAsState()
    val filterState by viewModel.filterState.collectAsState()
    val hotTags by viewModel.hotTags.collectAsState()
    val images = viewModel.images.collectAsLazyPagingItems()

    var showFilterSheet by remember { mutableStateOf(false) }
    val selectionState = remember { SelectionState() }
    var showBatchTagDialog by remember { mutableStateOf(false) }
    var showBatchRateDialog by remember { mutableStateOf(false) }
    var showBatchDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            if (selectionState.isSelectionMode) {
                TopAppBar(
                    title = {
                        Text(
                            "已选择 ${selectionState.selectedCount} 项",
                            style = MaterialTheme.typography.titleMedium
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { selectionState.clear() }) {
                            Icon(Icons.Default.Close, contentDescription = "取消选择")
                        }
                    },
                    actions = {
                        TextButton(onClick = {
                            val allIds = (0 until images.itemCount).mapNotNull { images[it]?.id }
                            selectionState.selectAll(allIds)
                        }) {
                            Text("全选")
                        }
                    }
                )
            } else {
                TopAppBar(
                    title = {
                        Text(
                            "CosPose",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    actions = {
                        IconButton(onClick = { showFilterSheet = true }) {
                            Icon(
                                Icons.Default.FilterList,
                                contentDescription = "筛选",
                                tint = if (filterState.isActive) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (selectionState.isSelectionMode) {
                BatchActionBar(
                    selectedCount = selectionState.selectedCount,
                    onSelectAll = {
                        val allIds = (0 until images.itemCount).mapNotNull { images[it]?.id }
                        selectionState.selectAll(allIds)
                    },
                    onClearSelection = { selectionState.clear() },
                    onBatchTag = { showBatchTagDialog = true },
                    onBatchRate = { showBatchRateDialog = true },
                    onBatchDelete = { showBatchDeleteDialog = true }
                )
            }
        },
        floatingActionButton = {
            if (!selectionState.isSelectionMode) {
                FloatingActionButton(
                    onClick = onUploadClick,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "上传")
                }
            }
        }
    ) { padding ->
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalItemSpacing = 8.dp
        ) {
            // Tagline
            item(span = StaggeredGridItemSpan.FullLine) {
                Text(
                    "汇聚Cosplay动作参考，激发你的拍摄灵感",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            // Hot tags
            if (hotTags.isNotEmpty()) {
                item(span = StaggeredGridItemSpan.FullLine) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        hotTags.forEach { tag ->
                            Text(
                                text = tag.name,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier
                                    .background(
                                        MaterialTheme.colorScheme.primaryContainer,
                                        RoundedCornerShape(16.dp)
                                    )
                                    .clickable { /* navigate to search */ }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // Sort tabs
            item(span = StaggeredGridItemSpan.FullLine) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    SortMode.entries.forEach { mode ->
                        val selected = mode == sortMode
                        TextButton(
                            onClick = {
                                viewModel.setSortMode(mode)
                            },
                            modifier = Modifier
                                .background(
                                    if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                    else MaterialTheme.colorScheme.surface,
                                    RoundedCornerShape(16.dp)
                                )
                        ) {
                            Text(
                                mode.label,
                                style = MaterialTheme.typography.labelLarge,
                                color = if (selected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Active filters display
            if (filterState.isActive) {
                item(span = StaggeredGridItemSpan.FullLine) {
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Filter count badge
                        InputChip(
                            selected = false,
                            onClick = { showFilterSheet = true },
                            label = {
                                Text(
                                    "${filterState.activeCount} 个筛选条件",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.FilterList,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            trailingIcon = {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "清除筛选",
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clickable { viewModel.clearFilters() }
                                )
                            },
                            colors = InputChipDefaults.inputChipColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )

                        // Rating filter chip
                        filterState.minRating?.let { min ->
                            InputChip(
                                selected = false,
                                onClick = { showFilterSheet = true },
                                label = {
                                    Text(
                                        "评分 ${"%.1f".format(min)}-${"%.1f".format(filterState.maxRating ?: 5f)}",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Star,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            )
                        }

                        // Aspect ratio chip
                        filterState.aspectRatio?.let { ratio ->
                            InputChip(
                                selected = false,
                                onClick = { showFilterSheet = true },
                                label = {
                                    Text(
                                        ratio.label,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            )
                        }

                        // MimeType chip
                        filterState.mimeType?.let { mime ->
                            val label = when (mime) {
                                "image/jpeg" -> "JPG"
                                "image/png" -> "PNG"
                                "image/webp" -> "WEBP"
                                else -> mime
                            }
                            InputChip(
                                selected = false,
                                onClick = { showFilterSheet = true },
                                label = {
                                    Text(
                                        label,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            )
                        }
                    }
                }
            }

            // Image grid
            items(
                count = images.itemCount,
                key = { index -> images[index]?.id ?: "img_$index" }
            ) { index ->
                val image = images[index]
                if (image != null) {
                    ImageCard(
                        image = image,
                        onClick = {
                            if (selectionState.isSelectionMode) {
                                selectionState.toggle(image.id)
                            } else {
                                onImageClick(image.id)
                            }
                        },
                        onLongClick = {
                            if (!selectionState.isSelectionMode) {
                                selectionState.toggle(image.id)
                            }
                        },
                        isSelected = selectionState.isSelected(image.id),
                        isSelectionMode = selectionState.isSelectionMode
                    )
                }
            }

            // Loading state
            when (images.loadState.append) {
                is LoadState.Loading -> {
                    item(span = StaggeredGridItemSpan.FullLine) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
                is LoadState.NotLoading -> {}
                is LoadState.Error -> {
                    item(span = StaggeredGridItemSpan.FullLine) {
                        Text(
                            "加载失败，点击重试",
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { images.retry() }
                                .padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            // Empty state
            if (images.loadState.refresh is LoadState.NotLoading && images.itemCount == 0) {
                item(span = StaggeredGridItemSpan.FullLine) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                if (filterState.isActive) "没有符合条件的图片" else "还没有图片",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                if (filterState.isActive) "尝试调整筛选条件" else "成为第一个上传的人吧！",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }

    // Filter bottom sheet
    if (showFilterSheet) {
        FilterBottomSheet(
            currentState = filterState,
            onApply = { state ->
                viewModel.setFilterState(state)
                showFilterSheet = false
            },
            onDismiss = { showFilterSheet = false }
        )
    }

    // Batch tag dialog
    if (showBatchTagDialog) {
        val allTags by viewModel.allTags.collectAsState()
        BatchTagDialog(
            availableTags = allTags,
            onApply = { tagIds ->
                viewModel.batchAddTags(selectionState.selectedIds.toList(), tagIds)
                selectionState.clear()
                showBatchTagDialog = false
            },
            onDismiss = { showBatchTagDialog = false }
        )
    }

    // Batch rate dialog
    if (showBatchRateDialog) {
        var batchRating by remember { mutableStateOf(5) }
        AlertDialog(
            onDismissRequest = { showBatchRateDialog = false },
            title = { Text("批量评分") },
            text = {
                Column {
                    Text("为选中的 ${selectionState.selectedCount} 张图片评分")
                    Spacer(modifier = Modifier.height(16.dp))
                    StarRating(
                        rating = batchRating,
                        onRatingChange = { batchRating = it }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.batchRate(selectionState.selectedIds.toList(), batchRating)
                        selectionState.clear()
                        showBatchRateDialog = false
                    }
                ) {
                    Text("应用")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBatchRateDialog = false }) {
                    Text("取消")
                }
            }
        )
    }

    // Batch delete dialog
    if (showBatchDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showBatchDeleteDialog = false },
            title = { Text("批量删除") },
            text = {
                Text("确定要删除选中的 ${selectionState.selectedCount} 张图片吗？此操作不可撤销。")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.batchDelete(selectionState.selectedIds.toList())
                        selectionState.clear()
                        showBatchDeleteDialog = false
                    }
                ) {
                    Text("删除", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showBatchDeleteDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}
