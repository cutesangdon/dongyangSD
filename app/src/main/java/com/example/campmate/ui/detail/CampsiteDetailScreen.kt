package com.example.campmate.ui.detail

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.campmate.data.model.Campsite
import com.example.campmate.data.model.CampsiteSite
import com.example.campmate.data.model.Review
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
    var adultCount by remember { mutableStateOf(1) }
    var childCount by remember { mutableStateOf(0) }
    val datePickerState = rememberDateRangePickerState()
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedSite by remember { mutableStateOf<CampsiteSite?>(null) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(campsite?.name ?: stringResource(R.string.loading)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    val startDate = datePickerState.selectedStartDateMillis
                    val endDate = datePickerState.selectedEndDateMillis
                    if (startDate != null && endDate != null && selectedSite != null) {
                        viewModel.makeReservation(adultCount, childCount, startDate, endDate, selectedSite!!.siteName)

                        // ✅✅✅ [수정됨] onClick 액션 공간에서는 context.getString()을 사용합니다. ✅✅✅
                        val message = context.getString(R.string.reservation_complete_message)
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        onNavigateUp()
                    } else {
                        // ✅✅✅ [수정됨] onClick 액션 공간에서는 context.getString()을 사용합니다. ✅✅✅
                        val message = context.getString(R.string.please_select_date_and_site)
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(50.dp)
            ) {
                Text(stringResource(R.string.reserve), fontSize = 18.sp)
            }
        }
    ) { paddingValues ->
        if (campsite == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            CampsiteDetailContent(
                modifier = Modifier.padding(paddingValues),
                campsite = campsite!!,
                reviews = reviews,
                adultCount = adultCount,
                childCount = childCount,
                onAdultChange = { adultCount = it },
                onChildChange = { childCount = it },
                onShowDatePicker = { showDatePicker = true },
                selectedDateRange = datePickerState.selectedStartDateMillis to datePickerState.selectedEndDateMillis,
                selectedSite = selectedSite,
                onSiteSelected = { site -> selectedSite = site }
            )
        }

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text(stringResource(R.string.ok)) }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text(stringResource(R.string.cancel)) }
                }
            ) {
                DateRangePicker(state = datePickerState)
            }
        }
    }
}

@Composable
fun CampsiteDetailContent(
    modifier: Modifier = Modifier,
    campsite: Campsite,
    reviews: List<Review>,
    adultCount: Int,
    childCount: Int,
    onAdultChange: (Int) -> Unit,
    onChildChange: (Int) -> Unit,
    onShowDatePicker: () -> Unit,
    selectedDateRange: Pair<Long?, Long?>,
    selectedSite: CampsiteSite?,
    onSiteSelected: (CampsiteSite) -> Unit
) {
    fun formatDate(millis: Long?): String {
        if (millis == null) return "YYYY-MM-DD"
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatter.format(Date(millis))
    }

    val dateRangeText = "${formatDate(selectedDateRange.first)} ~ ${formatDate(selectedDateRange.second)}"

    Column(modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Image(
            painter = rememberAsyncImagePainter(campsite.imageUrl),
            contentDescription = campsite.name,
            modifier = Modifier.fillMaxWidth().height(250.dp),
            contentScale = ContentScale.Crop
        )
        Column(modifier = Modifier.padding(16.dp)) {
            Text(campsite.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(campsite.address, style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
            Divider(Modifier.padding(vertical = 16.dp))
            Text(stringResource(R.string.select_date), style = MaterialTheme.typography.titleLarge)
            OutlinedButton(onClick = onShowDatePicker, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.DateRange, contentDescription = "Date Range")
                Spacer(Modifier.width(8.dp))
                Text(dateRangeText)
            }
            Spacer(Modifier.height(16.dp))
            Text(stringResource(R.string.select_site), style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            campsite.sites.forEach { site ->
                SiteItem(
                    site = site,
                    isSelected = site.siteId == selectedSite?.siteId,
                    onClick = { onSiteSelected(site) }
                )
            }
            Spacer(Modifier.height(16.dp))
            Text(stringResource(R.string.select_guests), style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            GuestCounter(stringResource(R.string.adult), adultCount, onAdultChange)
            Spacer(Modifier.height(8.dp))
            GuestCounter(stringResource(R.string.child), childCount, onChildChange)

            Divider(Modifier.padding(vertical = 16.dp))
            Text("방문자 리뷰", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            if (reviews.isEmpty()) {
                Text("아직 작성된 리뷰가 없습니다.")
            } else {
                reviews.forEach { review ->
                    // TODO: 리뷰 카드 UI를 별도로 만들어 재사용하면 좋습니다.
                    Text("${review.authorName}: ${review.content} (별점: ${review.rating})")
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun SiteItem(site: CampsiteSite, isSelected: Boolean, onClick: () -> Unit) {
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray

    Row(
        modifier = Modifier.fillMaxWidth().selectable(selected = isSelected, onClick = onClick).border(1.dp, borderColor, RoundedCornerShape(8.dp)).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(site.siteName, fontWeight = FontWeight.Bold)
        Text("${stringResource(R.string.nightly_price)} ${site.pricePerNight}원")
    }
    Spacer(Modifier.height(8.dp))
}

@Composable
fun GuestCounter(label: String, count: Int, onCountChange: (Int) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 18.sp)
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { if (count > 0) onCountChange(count - 1) }) {
                Icon(Icons.Default.Remove, contentDescription = stringResource(R.string.remove))
            }
            Text(count.toString(), fontSize = 18.sp, modifier = Modifier.padding(horizontal = 8.dp))
            IconButton(onClick = { onCountChange(count + 1) }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add))
            }
        }
    }
}