package com.example.campmate.data.model

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("description")
    val description: String,

    @SerializedName("icon")
    val icon: String,

    @SerializedName("temperature")
    val temperature: Double,

    @SerializedName("humidity")
    val humidity: Int,

    @SerializedName("dt_txt")
    val dt_txt: String // "2025-10-30 18:00:00"

)
