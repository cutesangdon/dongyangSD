package com.example.campmate.ui.weather

import android.Manifest
import android.text.format.DateUtils.isToday
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.campmate.data.model.WeatherResponse
import com.example.campmate.ui.weather.WeatherViewModel
import java.time.LocalDate
import java.time.LocalDateTime // ❗️ 시간 파싱용 임포트
import java.time.format.DateTimeFormatter

// --- 테마 색상 정의 (동일) ---
val theme_light_primary = Color(0xFF226C2E)
val theme_light_secondary = Color(0xFF53634B)
val theme_light_background = Color(0xFFF9FAEF)
val theme_light_surfaceVariant = Color(0xFFDEE5D9)
val theme_light_secondaryContainer = Color(0xFFD6E8CA)
val theme_light_onPrimary = Color.White
val theme_light_onSecondaryContainer = Color(0xFF111F0D)


@Composable
private fun getIconResource(iconId: String): Int {
    // ⬇️ 'R.drawable...' 대신 'com.example.campmate.R.drawable...'로
    //    전체 경로를 명시합니다.
    return when (iconId) {
        "01d" -> com.example.campmate.R.drawable.ic_weather_01d // 맑음 (낮) sun
        "01n" -> com.example.campmate.R.drawable.ic_weather_01n // 맑음 (밤) moon

        "02d" -> com.example.campmate.R.drawable.ic_weather_02d // 구름 조금 (낮) cloud
        "02n" -> com.example.campmate.R.drawable.ic_weather_02n // 구름 조금 (밤) cloudy

        "03d", "03n" -> com.example.campmate.R.drawable.ic_weather_03d // 구름 많음 (낮/밤 동일)
        "04d", "04n" -> com.example.campmate.R.drawable.ic_weather_04d // 흐림 (낮/밤 동일)

        "09d", "09n" -> com.example.campmate.R.drawable.ic_weather_09d // 소나기 (낮/밤 동일)

        "10d" -> com.example.campmate.R.drawable.ic_weather_10d // 비 (낮)
        "10n" -> com.example.campmate.R.drawable.ic_weather_10n // 비 (밤)

        "11d", "11n" -> com.example.campmate.R.drawable.ic_weather_11d // 천둥번개 (낮/밤 동일)
        "13d", "13n" -> com.example.campmate.R.drawable.ic_weather_13d // 눈 (낮/밤 동일)
        "50d", "50n" -> com.example.campmate.R.drawable.ic_weather_50d // 안개 (낮/밤 동일)

        else -> com.example.campmate.R.drawable.ic_weather_01d // 기본값
    }
}


@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel = hiltViewModel()
) {
    val forecastState by viewModel.forecastState.collectAsState()

    // 1. 위치 권한 런처 (동일)
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) ||
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)) {
            // 권한 승인
        }
    }

    // 2. Composable 시작 시 권한 요청 (동일)
    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    // 3. 새 테마 배경 적용
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(theme_light_background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- 4. ❗️ 위치 선택기(LocationSelector) 제거됨 ---

        // Spacer(modifier = Modifier.height(16.dp)) // 칩이 없으므로 간격 제거

        // --- 5. 날씨 정보 표시 ---
        if (forecastState.isLoading && forecastState.dailySummaries.isEmpty()) {
            // 초기 로딩 (동일)
            CircularProgressIndicator(
                modifier = Modifier.padding(top = 64.dp),
                color = theme_light_primary
            )
            Text(
                "날씨 정보 로딩 중...",
                color = theme_light_secondary,
                modifier = Modifier.padding(top = 16.dp)
            )
        } else if (forecastState.errorMessage != null) {
            // 에러 (동일)
            Text("오류: ${forecastState.errorMessage}", color = Color.Red)
        } else if (forecastState.dailySummaries.isNotEmpty()) {
            // --- 6. 데이터가 있을 때 ---

            // 6-1. (수정) 'selectedDay' 상태 관리 (UI에 표시할 하루치 요약)
            var selectedDay by remember(forecastState.dailySummaries) {
                mutableStateOf(forecastState.dailySummaries.first())
            }

            // 6-2. (신규) 'selectedHour' 상태 관리 (UI에 표시할 3시간짜리 상세)
            //      selectedDay가 바뀌면, 그 날의 첫 시간 데이터로 자동 리셋
            var selectedHour by remember(selectedDay) {
                mutableStateOf(selectedDay.hourlyData.first())
            }

            // 6-3. (수정) 메인 날씨 카드 - 'selectedDay' 요약이 아닌 'selectedHour' 상세를 표시
            WeatherDetailCard(
                hourlyData = selectedHour, // ⬅️ 시간별 데이터 전달
                locationName = forecastState.locationDisplayName, // "현재 위치"
                isLoading = forecastState.isLoading
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 6-4. (신규) 3시간 간격 "시간 선택" 스크롤
            Text("시간별 예보", style = MaterialTheme.typography.titleMedium, color = theme_light_secondary, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            HourlyForecastRow(
                hourlyDataList = selectedDay.hourlyData, // ⬅️ 'selectedDay'의 시간 목록을 전달
                selectedHour = selectedHour,
                onHourSelected = { newHour ->
                    selectedHour = newHour // ⬅️ 시간 선택 시 'selectedHour' 상태 업데이트
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 6-5. (기존) "날짜 선택" 스크롤
            Text("날짜별 예보", style = MaterialTheme.typography.titleMedium, color = theme_light_secondary, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            DailyForecastRow( // ⬅️ 이름 변경 (ForecastRow -> DailyForecastRow)
                summaries = forecastState.dailySummaries,
                selectedDate = selectedDay.date,
                onDaySelected = { newSelectedDay ->
                    selectedDay = newSelectedDay // ⬅️ 날짜 선택 시 'selectedDay' 상태 업데이트
                }
            )
        }
    }
}

/**
 * 1. ❗️ LocationSelector Composable 제거됨
 */

/**
 * 2. 메인 날씨 정보 카드 (수정: DailyForecastSummary -> WeatherResponse)
 */
@Composable
fun WeatherDetailCard(
    hourlyData: WeatherResponse,
    locationName: String,
    isLoading: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, theme_light_surfaceVariant, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = locationName,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = theme_light_primary
            )

            // ⬅️ 2. 날짜 대신 '시간' 표시
            val timeFormatter = DateTimeFormatter.ofPattern("M월 d일 HH:mm")
            val dateTime = LocalDateTime.parse(hourlyData.dt_txt, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            Text(
                text = dateTime.format(timeFormatter), // "11월 13일 18:00"
                fontSize = 16.sp,
                color = theme_light_secondary
            )
            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier.size(120.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = getIconResource(hourlyData.icon)),
                    contentDescription = hourlyData.description,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                if (isLoading) {
                    CircularProgressIndicator(color = theme_light_primary)
                }
            }

            // ⬅️ 3. 평균 온도 대신 '현재' 온도 표시
            Text(
                text = "${hourlyData.temperature.toInt()}°",
                fontSize = 60.sp,
                fontWeight = FontWeight.ExtraBold,
                color = theme_light_primary
            )
            Text(
                text = hourlyData.description,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = theme_light_secondary
            )
            Spacer(modifier = Modifier.height(16.dp))

            // ⬅️ 4. 최고/최저 대신 '습도' 표시 (WeatherInfoChip은 재사용)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                WeatherInfoChip(label = "습도", value = "${hourlyData.humidity}%")
                // (참고: DTO에 feels_like가 있다면 여기에 체감온도 추가 가능)
            }
        }
    }
}

/**
 * 3. (신규) 3시간 간격 '시간' 스크롤
 */
@Composable
fun HourlyForecastRow(
    hourlyDataList: List<WeatherResponse>,
    selectedHour: WeatherResponse,
    onHourSelected: (WeatherResponse) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(hourlyDataList) { hourData ->
            HourlyForecastItem( // ⬅️ 시간별 아이템 Composable
                data = hourData,
                isSelected = hourData == selectedHour,
                onClick = { onHourSelected(hourData) }
            )
        }
    }
}

/**
 * 4. (신규) '시간' 스크롤의 개별 아이템
 */
@Composable
fun HourlyForecastItem(
    data: WeatherResponse,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) theme_light_primary else theme_light_secondaryContainer
    val textColor = if (isSelected) theme_light_onPrimary else theme_light_onSecondaryContainer

    // 시간 파싱
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val dateTime = LocalDateTime.parse(data.dt_txt, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(vertical = 16.dp, horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = dateTime.format(timeFormatter), // "18:00"
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
        Image(
            painter = painterResource(id = getIconResource(data.icon)),
            contentDescription = null,
            modifier = Modifier.size(40.dp),
//            colorFilter = ColorFilter.tint(textColor)
        )
        Text(
            text = "${data.temperature.toInt()}°",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}


/**
 * 5. (이름 변경) 5일 예보 '날짜' 스크롤
 */
@Composable
fun DailyForecastRow( // ⬅️ 이름 변경 (ForecastRow -> DailyForecastRow)
    summaries: List<DailyForecastSummary>,
    selectedDate: LocalDate?,
    onDaySelected: (DailyForecastSummary) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(summaries) { daySummary ->
            ForecastDayItem( // ⬅️ 이 Composable은 기존 것 그대로 사용
                summary = daySummary,
                isSelected = daySummary.date == selectedDate,
                onClick = { onDaySelected(daySummary) }
            )
        }
    }
}

/**
 * 6. (기존) '날짜' 스크롤의 개별 아이템 (동일)
 */
@Composable
fun ForecastDayItem(
    summary: DailyForecastSummary,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) {
        theme_light_primary
    } else {
        theme_light_secondaryContainer
    }
    val textColor = if (isSelected) {
        theme_light_onPrimary
    } else {
        theme_light_onSecondaryContainer
    }

    val isToday = summary.date == LocalDate.now()
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(vertical = 16.dp, horizontal = 20.dp)
            .animateContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (isToday) "오늘" else summary.dayOfWeek,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
        Image(
            painter = painterResource(id = getIconResource(summary.icon)),
            contentDescription = null,
            modifier = Modifier.size(40.dp),
//            colorFilter = ColorFilter.tint(textColor)
        )
        Text(
            text = "${summary.tempMax.toInt()}°",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
        Text(
            text = "${summary.tempMin.toInt()}°",
            fontSize = 14.sp,
            color = textColor.copy(alpha = 0.7f)
        )
    }
}

/**
 * 7. (기존) 최고/최저 기온 칩 (동일)
 */
@Composable
fun WeatherInfoChip(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = theme_light_secondary.copy(alpha = 0.8f)
        )
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = theme_light_secondary
        )
    }
}