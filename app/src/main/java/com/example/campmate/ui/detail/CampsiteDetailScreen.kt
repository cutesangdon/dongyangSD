package com.example.campmate.ui.detail

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.campmate.R
import com.example.campmate.data.model.CampsiteSite
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampsiteDetailScreen(
    onNavigateUp: () -> Unit,
    viewModel: CampsiteDetailViewModel = hiltViewModel()
) {
    val campsite by viewModel.campsite.collectAsState()
    val reviews by viewModel.reviews.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val context = LocalContext.current

    var adultCount by remember { mutableStateOf(1) }
    var childCount by remember { mutableStateOf(0) }
    var selectedSite by remember { mutableStateOf<CampsiteSite?>(null) }

    // 날짜 상태
    var selectedStartDateMillis by remember { mutableStateOf<Long?>(null) }
    var selectedEndDateMillis by remember { mutableStateOf<Long?>(null) }

    // 다이얼로그 표시 여부
    var showStartDateDialog by remember { mutableStateOf(false) }
    var showEndDateDialog by remember { mutableStateOf(false) }

    val dateFormatter = remember { SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()) }

    // 예약 결과 처리
    LaunchedEffect(Unit) {
        viewModel.reservationResult.collectLatest { success ->
            if (success) {
                Toast.makeText(context, context.getString(R.string.reservation_complete_message), Toast.LENGTH_SHORT).show()
                onNavigateUp()
            } else {
                Toast.makeText(context, "예약에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(campsite?.name ?: "상세보기") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로가기")
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    val startDate = selectedStartDateMillis
                    val endDate = selectedEndDateMillis
                    if (startDate != null && endDate != null && selectedSite != null) {
                        viewModel.makeReservation(adultCount, childCount, startDate, endDate, selectedSite!!)
                    } else {
                        Toast.makeText(context, context.getString(R.string.please_select_date_and_site), Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(16.dp).height(50.dp)
            ) {
                Text(stringResource(R.string.reserve), fontSize = 18.sp)
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) {
            when {
                isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                error != null -> Text(text = error!!, color = Color.Red, modifier = Modifier.align(Alignment.Center).padding(16.dp))
                campsite != null -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        // 1. 이미지
                        item {
                            Image(
                                painter = rememberAsyncImagePainter(campsite!!.imageUrl),
                                contentDescription = campsite!!.name,
                                modifier = Modifier.fillMaxWidth().height(250.dp),
                                contentScale = ContentScale.Crop
                            )
                        }
                        // 2. 설명
                        item {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(campsite!!.name ?: "", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(campsite!!.description ?: "", style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                        item { HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp)) }

                        // --- 날짜 선택 섹션 ---
                        item {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("날짜 선택", style = MaterialTheme.typography.titleLarge)
                                Spacer(modifier = Modifier.height(16.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceAround,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // 체크인 버튼
                                    Button(
                                        onClick = {
                                            // 🛠️ 디버깅용 토스트: 버튼이 눌리는지 확인
//                                             Toast.makeText(context, "체크인 클릭됨", Toast.LENGTH_SHORT).show()
                                            showStartDateDialog = true
                                        },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(selectedStartDateMillis?.let { dateFormatter.format(Date(it)) } ?: "체크인")
                                    }

                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text("~", fontSize = 18.sp)
                                    Spacer(modifier = Modifier.width(16.dp))

                                    // 체크아웃 버튼
                                    Button(
                                        onClick = {
                                            if (selectedStartDateMillis == null) {
                                                Toast.makeText(context, "체크인 날짜를 먼저 선택해주세요.", Toast.LENGTH_SHORT).show()
                                            } else {
                                                showEndDateDialog = true
                                            }
                                        },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(selectedEndDateMillis?.let { dateFormatter.format(Date(it)) } ?: "체크아웃")
                                    }
                                }
                            }
                        }
                        item { HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) }

                        // 4. 인원 선택
                        item {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("인원 선택", style = MaterialTheme.typography.titleLarge)
                                Spacer(modifier = Modifier.height(8.dp))
                                GuestCounter("성인", adultCount) { adultCount = it }
                                Spacer(modifier = Modifier.height(8.dp))
                                GuestCounter("아동", childCount) { childCount = it }
                            }
                        }
                        item { HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) }

                        // 5. 사이트 선택
                        item {
                            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                                Text("사이트 선택", style = MaterialTheme.typography.titleLarge)
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                        items(campsite!!.sites) { site ->
                            SiteItem(
                                site = site,
                                isSelected = site == selectedSite,
                                onClick = { selectedSite = site },
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        item { HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) }

                        // 6. 리뷰
                        item {
                            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                                Text("방문자 리뷰 (${reviews.size}개)", style = MaterialTheme.typography.titleLarge)
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                        items(reviews) { review ->
                            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                                Text("${review.authorName ?: "익명"}: (평점: ${review.rating ?: "-"})")
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(review.content ?: "", style = MaterialTheme.typography.bodyMedium)
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                        item { Spacer(modifier = Modifier.height(20.dp)) }
                    }
                }
            }
        }
    } // ⬅️ Scaffold 끝

    // ------------------------------------------------------------------------
    // ❗️ 다이얼로그 코드는 반드시 Scaffold 밖, 함수 최하단에 배치하세요.
    // ------------------------------------------------------------------------

    if (showStartDateDialog) {
        val datePickerState = rememberDatePickerState()

        DatePickerDialog(
            onDismissRequest = { showStartDateDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedStartDateMillis = datePickerState.selectedDateMillis
                    selectedEndDateMillis = null
                    showStartDateDialog = false
                }) { Text("확인") }
            },
            dismissButton = {
                TextButton(onClick = { showStartDateDialog = false }) { Text("취소") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showEndDateDialog) {
        val datePickerState = rememberDatePickerState(
            // 체크아웃은 체크인 다음날부터 시작하도록 초기값 설정
            initialSelectedDateMillis = (selectedStartDateMillis ?: System.currentTimeMillis()) + 86400000
        )

        DatePickerDialog(
            onDismissRequest = { showEndDateDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedEndDateMillis = datePickerState.selectedDateMillis
                    showEndDateDialog = false
                }) { Text("확인") }
            },
            dismissButton = {
                TextButton(onClick = { showEndDateDialog = false }) { Text("취소") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

} // ⬅️ CampsiteDetailScreen 끝

// --- 하위 Composable ---

@Composable
fun SiteItem(site: CampsiteSite, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        val siteNameText = site.name ?: "이름 없음"
        val priceText = site.price?.let { "${it}원" } ?: "가격 정보 없음"
        Text("$siteNameText - $priceText")
    }
}

@Composable
fun GuestCounter(label: String, count: Int, onCountChange: (Int) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 18.sp)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = { if (count > 0) onCountChange(count - 1) }) { Text("-") }
            Text(count.toString(), modifier = Modifier.padding(horizontal = 16.dp), fontSize = 18.sp)
            Button(onClick = { onCountChange(count + 1) }) { Text("+") }
        }
    }
}