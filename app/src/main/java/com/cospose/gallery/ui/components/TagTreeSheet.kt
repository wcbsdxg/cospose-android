package com.cospose.gallery.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cospose.gallery.data.db.entity.TagEntity

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TagTreeSheet(
    allTags: List<TagEntity>,
    selectedTagIds: Set<String>,
    onTagToggle: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var searchQuery by remember { mutableStateOf("") }
    var expandedCategories by remember { mutableStateOf(setOf<String>()) }

    val filteredTags = if (searchQuery.isBlank()) {
        allTags
    } else {
        allTags.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.category.contains(searchQuery, ignoreCase = true)
        }
    }

    // Group by category
    val groupedTags = filteredTags.groupBy { it.category }

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
                "管理标签",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Search
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("搜索标签") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Selected tags
            if (selectedTagIds.isNotEmpty()) {
                Text(
                    "已选择 ${selectedTagIds.size} 个标签",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(6.dp),
                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(4.dp)
                ) {
                    allTags
                        .filter { it.id in selectedTagIds }
                        .forEach { tag ->
                            TagGroupChip(
                                tag = tag,
                                onClick = { onTagToggle(tag.id) },
                                onRemove = { onTagToggle(tag.id) }
                            )
                        }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Tag tree
            LazyColumn(
                modifier = Modifier.height(400.dp)
            ) {
                groupedTags.forEach { (category, tags) ->
                    val isExpanded = expandedCategories.contains(category)

                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    expandedCategories = if (isExpanded) {
                                        expandedCategories - category
                                    } else {
                                        expandedCategories + category
                                    }
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TagGroupHeader(
                                category = category,
                                count = tags.size,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = {
                                    expandedCategories = if (isExpanded) {
                                        expandedCategories - category
                                    } else {
                                        expandedCategories + category
                                    }
                                }
                            ) {
                                Icon(
                                    if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = if (isExpanded) "收起" else "展开"
                                )
                            }
                        }
                    }

                    if (isExpanded) {
                        items(tags) { tag ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onTagToggle(tag.id) }
                                    .padding(start = 24.dp, top = 4.dp, bottom = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = tag.id in selectedTagIds,
                                    onCheckedChange = { onTagToggle(tag.id) }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    tag.name,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
