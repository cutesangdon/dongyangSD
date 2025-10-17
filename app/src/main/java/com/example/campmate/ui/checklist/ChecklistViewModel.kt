// ChecklistViewModel.kt

package com.example.campmate.ui.checklist

import androidx.lifecycle.ViewModel
import com.example.campmate.data.model.ChecklistItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ChecklistViewModel @Inject constructor() : ViewModel() {

    private val _items = MutableStateFlow<List<ChecklistItem>>(emptyList())
    val items: StateFlow<List<ChecklistItem>> = _items.asStateFlow()

    init {
        // 초기 데이터 (필요 시 유지)
        _items.value = listOf(
            ChecklistItem(1, "텐트", true),
            ChecklistItem(2, "침낭", false),
            ChecklistItem(3, "랜턴", false),
        )
    }

    fun addItem(text: String) {
        if (text.isNotBlank()) {
            val newItem = ChecklistItem(id = (_items.value.maxOfOrNull { it.id } ?: 0) + 1, text = text)
            _items.update { currentList -> currentList + newItem }
        }
    }

    fun toggleChecked(itemId: Int) {
        _items.update { currentList ->
            currentList.map { item ->
                if (item.id == itemId) item.copy(isChecked = !item.isChecked) else item
            }
        }
    }

    fun removeCheckedItems() {
        _items.update { currentList ->
            // isChecked가 false인 아이템만 남기고 나머지는 필터링(삭제)합니다.
            currentList.filter { !it.isChecked }
        }
    }
}