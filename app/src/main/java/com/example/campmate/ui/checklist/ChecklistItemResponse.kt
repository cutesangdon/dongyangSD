package com.example.campmate.data.model

import com.google.gson.annotations.SerializedName

data class ChecklistItemResponse(
    @SerializedName("id")
    val id: Long,

    @SerializedName("itemName")
    val itemName: String,

    @SerializedName("isChecked")
    val isChecked: Boolean
)

