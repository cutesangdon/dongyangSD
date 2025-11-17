package com.example.campmate.ui.weather

import com.example.campmate.data.model.WeatherResponse
import java.time.LocalDate

data class DailyForecastSummary(
    val date: LocalDate,        // 날짜 (예: 2025-10-30)
    val dayOfWeek: String,      // 요일 (예: "목")
    val icon: String,           // 대표 아이콘 (예: "01d")
    val description: String,    // 대표 날씨 (예: "맑음")
    val tempMin: Double,        // 그날의 최저 기온
    val tempMax: Double,        // 그날의 최고 기온

    // 이 날짜에 해당하는 3시간 간격 원본 데이터 (선택 시 상세 정보 표시용)
    val hourlyData: List<WeatherResponse>
)
