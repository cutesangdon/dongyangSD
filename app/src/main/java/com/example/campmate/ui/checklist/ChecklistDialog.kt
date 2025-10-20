package com.example.campmate.ui.checklist

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.campmate.data.model.ChecklistItem

/**
 * 체크리스트 전체를 보여주고 관리하는 다이얼로그 Composable
 */
@Composable
fun ChecklistDialog(
    onDismiss: () -> Unit,
    viewModel: ChecklistViewModel = hiltViewModel()
) {
    val items by viewModel.items.collectAsState()
    val presets by viewModel.presets.collectAsState()
    var showAddItemDialog by remember { mutableStateOf(false) }
    var showPresetDialog by remember { mutableStateOf(false) }

    // ✅✅✅ [해결] 이전에 오류가 발생했던 부분입니다. ✅✅✅
    // viewModel.isLoadingPresets -> viewModel.isLoading 으로 수정합니다.
    val isLoadingPresets by viewModel.isLoading.collectAsState()


    if (showAddItemDialog) {
        AddItemDialog(
            onDismiss = { showAddItemDialog = false },
            onAddItem = { text ->
                viewModel.addItem(text)
                showAddItemDialog = false
            }
        )
    }

    if (showPresetDialog) {
        PresetDialog(
            presets = presets,
            currentItems = items,
            isLoading = isLoadingPresets, // 수정된 변수를 전달합니다.
            onDismiss = { showPresetDialog = false },
            onAddItem = { itemName ->
                viewModel.addPresetItem(itemName)
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("캠핑 준비물 체크리스트") },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(items) { item ->
                    ChecklistItemRow(
                        item = item,
                        onCheckedChange = { viewModel.toggleChecked(item.id) }
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = { showAddItemDialog = true }) {
                Text("직접 추가")
            }
        },
        dismissButton = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = { showPresetDialog = true }) {
                    Text("프리셋")
                }
                IconButton(onClick = { viewModel.removeCheckedItems() }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Checked Items")
                }
                TextButton(onClick = onDismiss) {
                    Text("닫기")
                }
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PresetDialog(
    presets: Map<String, List<String>>,
    currentItems: List<ChecklistItem>,
    isLoading: Boolean, // 로딩 상태를 파라미터로 받습니다.
    onDismiss: () -> Unit,
    onAddItem: (itemName: String) -> Unit
) {
    val currentItemTexts = remember(currentItems) { currentItems.map { it.text } }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("프리셋 불러오기") },
        text = {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                if (isLoading) {
                    CircularProgressIndicator()
                } else if (presets.isEmpty()) {
                    Text("불러올 프리셋이 없습니다.")
                } else {
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        presets.forEach { (category, items) ->
                            stickyHeader {
                                Surface(modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.surfaceVariant) {
                                    val displayName = when (category) {
                                        "basic" -> "기본 준비물"
                                        "cooking" -> "취사 도구"
                                        "electrical" -> "전기용품"
                                        "etc" -> "기타"
                                        else -> category
                                    }
                                    Text(
                                        text = displayName,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
                                    )
                                }
                            }

                            items(items) { itemName ->
                                val isAdded = itemName in currentItemTexts
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = itemName)
                                    Button(
                                        onClick = { onAddItem(itemName) },
                                        enabled = !isAdded
                                    ) {
                                        Text(if (isAdded) "추가됨" else "추가")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("닫기")
            }
        }
    )
}


@Composable
private fun AddItemDialog(
    onDismiss: () -> Unit,
    onAddItem: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("준비물 추가") },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("준비물 이름") },
                singleLine = true
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    if (text.isNotBlank()) {
                        onAddItem(text)
                    }
                }
            ) { Text("추가") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("취소") }
        }
    )
}

@Composable
private fun ChecklistItemRow(
    item: ChecklistItem,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = item.isChecked,
            onCheckedChange = onCheckedChange
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = item.text, style = MaterialTheme.typography.bodyLarge)
    }
}

