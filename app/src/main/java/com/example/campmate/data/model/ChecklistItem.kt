// ChecklistItem.kt

package com.example.campmate.data.model

data class ChecklistItem(
    val id: Int,
    val text: String,
    val isChecked: Boolean = false
)