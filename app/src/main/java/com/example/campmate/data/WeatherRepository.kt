package com.example.campmate.data

import com.example.campmate.data.model.WeatherResponse
import com.example.campmate.data.remote.ApiService
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val apiService: ApiService
    // 1. LocationRepository 의존성 제거
    // private val locationRepository: LocationRepository
) {
    /**
     * 5일 예보를 가져옵니다 (3시간 간격 데이터 40개)
     * (수정) ViewModel에서 직접 위도(lat), 경도(lon)를 받아옵니다.
     */
    // 2. lat, lon 파라미터 추가
    suspend fun fetchForecast(lat: Double, lon: Double): Result<List<WeatherResponse>> {
        return try {

            // 3. 파라미터로 받은 lat, lon을 API에 직접 전달
            val forecastData = apiService.getForecast(
                lat = lat,
                lon = lon
            )
            Result.success(forecastData)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}