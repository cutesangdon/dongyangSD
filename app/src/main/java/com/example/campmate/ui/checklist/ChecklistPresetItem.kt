package com.example.campmate.data.model

import com.google.gson.annotations.SerializedName

data class ChecklistPresetItem(
    @SerializedName("id")
    val id: Long,

    @SerializedName("category")
    val category: String,

    @SerializedName("itemName")
    val itemName: String
)
