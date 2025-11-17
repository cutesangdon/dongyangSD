package com.example.campmate.ui.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campmate.data.LocationRepository
import com.example.campmate.data.WeatherRepository
import com.example.campmate.data.model.WeatherResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.Duration // ⬅️ 1. Duration 임포트
import java.time.LocalDate
import java.time.LocalDateTime // ⬅️ 2. LocalDateTime 임포트
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject

// --- UI에 표시할 모든 데이터를 담는 상태 클래스 ---

/**
 * 날씨 예보의 '하루치' 요약 데이터 모델 (UI용)
 * (DailyForecastSummary.kt 파일에 정의된 것을 사용)
 */

/**
 * 날씨 화면의 전체 UI 상태 (단순화됨)
 */
data class ForecastUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val dailySummaries: List<DailyForecastSummary> = emptyList(), // 요약
    val locationDisplayName: String = "날씨 로딩 중..."
)

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _forecastState = MutableStateFlow(ForecastUiState())
    val forecastState: StateFlow<ForecastUiState> = _forecastState

    // ⬅️ 3. dateTimeFormatter를 클래스 멤버로 이동 (헬퍼 함수에서 사용)
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    private val dayOfWeekFormatter = { date: LocalDate ->
        date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN)
    }

    init {
        // 앱 시작 시 날씨 로드
        loadInitialWeather()
    }

    /**
     * (수정) 현재 위치의 날씨만 가져오도록 로직 변경
     */
    private fun loadInitialWeather() {
        viewModelScope.launch {
            _forecastState.value = ForecastUiState(isLoading = true)

            try {
                // 1. 현재 위치 정보 가져오기
                val currentLocation = locationRepository.getCurrentLocation()

                if (currentLocation == null) {
                    _forecastState.value = ForecastUiState(
                        isLoading = false,
                        errorMessage = "위치 정보를 가져올 수 없습니다. 권한을 확인해주세요."
                    )
                    return@launch
                }

                // 2. 현재 위치의 날씨 정보 로드
                fetchWeatherForLocation(currentLocation.latitude, currentLocation.longitude)

            } catch (e: Exception) {
                _forecastState.value = ForecastUiState(isLoading = false, errorMessage = e.message ?: "알 수 없는 오류 발생")
            }
        }
    }

    /**
     * (수정) 특정 위치의 날씨를 가져오고 UI 상태를 업데이트하는 공통 함수
     */
    private suspend fun fetchWeatherForLocation(lat: Double, lon: Double) {
        weatherRepository.fetchForecast(lat, lon)
            .onSuccess { fullForecastList ->
                // ⬅️ 4. 여기서 'processForecast' (보간 로직 포함) 호출
                val dailySummaries = processForecast(fullForecastList)
                _forecastState.value = ForecastUiState(
                    isLoading = false,
                    dailySummaries = dailySummaries,
                    locationDisplayName = "현재 위치"
                )
            }
            .onFailure { error ->
                _forecastState.value = _forecastState.value.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "날씨 정보 로드 실패"
                )
            }
    }

    // ⬅️ 5. (신규) 'dt_txt'를 LocalDateTime으로 파싱하는 헬퍼
    private fun parseDateTime(dtTxt: String): LocalDateTime {
        return LocalDateTime.parse(dtTxt, dateTimeFormatter)
    }

    // ⬅️ 6. (신규) LocalDateTime 확장 함수
    private fun LocalDateTime.isBeforeOrEqual(other: LocalDateTime): Boolean = !this.isAfter(other)
    private fun LocalDateTime.isAfterOrEqual(other: LocalDateTime): Boolean = !this.isBefore(other)

    // ⬅️ 7. (신규) 3시간 간격 데이터를 2시간 간격으로 보간하는 함수
    private fun interpolateListToTwoHour(originalList: List<WeatherResponse>): List<WeatherResponse> {
        if (originalList.isEmpty()) return emptyList()

        val newList = mutableListOf<WeatherResponse>()
        val startTime = parseDateTime(originalList.first().dt_txt)
        val endTime = parseDateTime(originalList.last().dt_txt)

        var currentTime = startTime

        while (!currentTime.isAfter(endTime)) {
            // 2시간 간격의 시간 (e.g., 09:00, 11:00, 13:00...)

            // 이 시간이 3시간 데이터 중 어느 사이에 끼어있는지 찾기
            val prev = originalList.lastOrNull { parseDateTime(it.dt_txt).isBeforeOrEqual(currentTime) }
            val next = originalList.firstOrNull { parseDateTime(it.dt_txt).isAfterOrEqual(currentTime) }

            when {
                // 1. 3시간 데이터와 정확히 일치 (e.g., 09:00, 12:00)
                (prev != null && parseDateTime(prev.dt_txt) == currentTime) -> newList.add(prev)
                (next != null && parseDateTime(next.dt_txt) == currentTime) -> newList.add(next)

                // 2. 두 3시간 데이터 사이 (e.g., 11:00는 09:00와 12:00 사이)
                (prev != null && next != null) -> {
                    val prevTime = parseDateTime(prev.dt_txt)
                    val nextTime = parseDateTime(next.dt_txt)

                    val totalDuration = Duration.between(prevTime, nextTime).toMinutes().toDouble()
                    val currentDuration = Duration.between(prevTime, currentTime).toMinutes().toDouble()

                    val ratio = if (totalDuration == 0.0) 0.0 else currentDuration / totalDuration

                    // 3. 온도와 습도를 비례 배분하여 '추측'
                    val temp = prev.temperature + (next.temperature - prev.temperature) * ratio
                    val humidity = prev.humidity + (next.humidity - prev.humidity) * ratio

                    // 4. 추측한 값으로 '가상'의 WeatherResponse 객체를 생성
                    val interpolatedResponse = WeatherResponse(
                        description = prev.description, // 아이콘과 설명은 이전 값을 그대로 사용
                        icon = prev.icon,
                        temperature = temp,
                        humidity = humidity.toInt(),
                        dt_txt = currentTime.format(dateTimeFormatter) // e.g., "... 11:00:00"
                    )
                    newList.add(interpolatedResponse)
                }
            }
            // 5. 다음 2시간 뒤 시간으로 이동
            currentTime = currentTime.plusHours(2)
        }
        return newList.distinctBy { it.dt_txt } // 중복 제거
    }


    /**
     * (수정) 3시간->2시간 보간 로직을 포함하도록 수정
     */
    private fun processForecast(fullList: List<WeatherResponse>): List<DailyForecastSummary> {

        if (fullList.isEmpty()) return emptyList()

        // ⬅️ 8. (신규) 3시간 리스트를 2시간 리스트로 변환
        val interpolatedList = interpolateListToTwoHour(fullList)

        // 1. 날짜(LocalDate)별로 그룹화 (이제 2시간 간격 리스트를 사용)
        val groupedByDate = interpolatedList.groupBy {
            parseDateTime(it.dt_txt).toLocalDate()
        }

        // 2. 그룹화된 데이터를 DailyForecastSummary로 변환
        return groupedByDate.map { (date, hourlyList) ->
            // (이하는 기존과 동일)
            val representativeWeather = hourlyList.getOrNull(hourlyList.size / 2)
            val minTemp = hourlyList.minOf { it.temperature }
            val maxTemp = hourlyList.maxOf { it.temperature }

            DailyForecastSummary(
                date = date,
                dayOfWeek = dayOfWeekFormatter(date),
                icon = representativeWeather?.icon ?: hourlyList.first().icon,
                description = representativeWeather?.description ?: hourlyList.first().description,
                tempMin = minTemp,
                tempMax = maxTemp,
                hourlyData = hourlyList // ⬅️ 2시간 간격 데이터가 전달됨
            )
        }.sortedBy { it.date }
    }
}