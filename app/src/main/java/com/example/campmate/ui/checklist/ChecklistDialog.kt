package com.example.campmate.ui.checklist

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.campmate.data.model.ChecklistItem
import kotlinx.coroutines.flow.collectLatest

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
    val isLoadingPresets by viewModel.isLoading.collectAsState()

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.errorEvent.collectLatest { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

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
            isLoading = isLoadingPresets,
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
                    // ✅ [수정] 개수 변경 콜백을 ViewModel과 연결
                    ChecklistItemRow(
                        item = item,
                        onCheckedChange = { viewModel.toggleChecked(item.id) },
                        onQuantityChange = { change -> viewModel.updateItemQuantity(item.id, change) }
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
    isLoading: Boolean,
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
    onCheckedChange: (Boolean) -> Unit,
    onQuantityChange: (Int) -> Unit // -1 또는 1 값을 받는 콜백
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = item.isChecked,
            onCheckedChange = onCheckedChange
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = item.text, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { onQuantityChange(-1) }, enabled = item.total > 1) {
                Icon(Icons.Default.Remove, contentDescription = "수량 감소")
            }
            Text(text = "${item.total}", style = MaterialTheme.typography.bodyLarge)
            IconButton(onClick = { onQuantityChange(1) }) {
                Icon(Icons.Default.Add, contentDescription = "수량 증가")
            }
        }
    }
}