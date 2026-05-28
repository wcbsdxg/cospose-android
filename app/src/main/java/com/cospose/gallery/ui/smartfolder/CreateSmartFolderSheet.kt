package com.cospose.gallery.ui.smartfolder

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cospose.gallery.ui.components.FilterBottomSheet
import com.cospose.gallery.ui.home.FilterState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateSmartFolderSheet(
    onCreate: (String, FilterState) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var folderName by remember { mutableStateOf("") }
    var showFilterSheet by remember { mutableStateOf(false) }
    var filterState by remember { mutableStateOf(FilterState()) }

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
                "创建智能文件夹",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = folderName,
                onValueChange = { folderName = it },
                label = { Text("文件夹名称") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = { showFilterSheet = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (filterState.isActive) "已设置筛选条件 (${filterState.activeCount} 项)"
                    else "设置筛选条件"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = {
                    if (folderName.isNotBlank() && filterState.isActive) {
                        onCreate(folderName, filterState)
                    }
                },
                enabled = folderName.isNotBlank() && filterState.isActive,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("创建")
            }
        }
    }

    if (showFilterSheet) {
        FilterBottomSheet(
            currentState = filterState,
            onApply = { state ->
                filterState = state
                showFilterSheet = false
            },
            onDismiss = { showFilterSheet = false }
        )
    }
}
