package com.example.campmate.ui.home

import android.R.attr.fontWeight
import androidx.compose.foundation.BorderStroke // (추가)
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable // (추가)
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells // (추가)
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid // (추가)
import androidx.compose.foundation.lazy.grid.items // (추가)
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface // (추가)
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource // (추가)
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.campmate.data.model.Campsite
import com.example.campmate.data.model.AdminZoneGroup
import com.example.campmate.ui.home.CampingTheme // (추가)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onCampsiteClick: (Long) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // (수정) ViewModel로부터 테마 리스트와 선택된 테마 상태를 가져옵니다
    val themes = viewModel.themes
    val selectedTheme by viewModel.selectedTheme.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        when (val state = uiState) {
            is HomeUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            is HomeUiState.Success -> {

                val allCampsites = state.adminGroups.firstOrNull()?.sites ?: emptyList()

                Column {
                    // (수정) 테마 필터칩 LazyRow -> 테마 아이콘 그리드
                    ThemeIconGrid(
                        themes = themes,
                        selectedTheme = selectedTheme,
                        onThemeSelected = { themeName ->
                            viewModel.onThemeSelected(themeName) // ViewModel의 이벤트 핸들러 호출
                        }
                    )

                    CampsiteList(
                        campsites = allCampsites,
                        onCampsiteClick = onCampsiteClick
                    )
                }
            }
            is HomeUiState.Error -> {
                Text(
                    text = state.message,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

/**
 * (신규) 테마 아이콘 그리드를 표시하는 Composable
 */
@Composable
fun ThemeIconGrid(
    themes: List<CampingTheme>,
    selectedTheme: String?,
    onThemeSelected: (String) -> Unit
) {
    //25.11.14 KM 수정
    LazyRow (
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp), // 아이템 간 가로 간격
        // 높이 제한을 해제하고 콘텐츠 크기에 맞춥니다 (Modifier.height(200.dp) 제거)
        modifier = Modifier.fillMaxWidth()
    ) {
        items(themes) { theme ->
            ThemeIconItem(
                theme = theme,
                isSelected = (theme.name == selectedTheme), // 현재 테마가 선택되었는지 여부
                onClick = { onThemeSelected(theme.name) } // 클릭 시 ViewModel에 이름 전달
            )
        }
    }
}

/**
 * (신규) 개별 테마 아이콘 아이템 UI
 */
@Composable
fun ThemeIconItem(
    theme: CampingTheme,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clickable(onClick = onClick)
            .width(85.dp)
    ) {
        // 아이콘을 담는 둥근 배경
        Surface(
            shape = RoundedCornerShape(24.dp), // 이미지의 둥근 모서리
            // (수정) 연한 배경색 (녹색 테마와 어울리도록)
            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
            modifier = Modifier.size(72.dp),
            // (수정) 선택 시 테두리 (녹색 테마 primary 색상 사용)
            border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
        ) {
            Image(
                // (수정) painterResource로 drawable 아이콘 로드
                painter = painterResource(id = theme.iconRes),
                contentDescription = theme.name,
                modifier = Modifier.padding(12.dp), // 아이콘 내부 패딩
                contentScale = ContentScale.Fit
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        // 텍스트
        Text(
            text = theme.name,
            style = MaterialTheme.typography.labelMedium,
            // (수정) 선택 시 굵게 및 primary 색상으로 변경
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
        )
    }
}


/**
 * (기존) CampsiteList Composable (변경 없음)
 */
@Composable
fun CampsiteList(
    campsites: List<Campsite>,
    onCampsiteClick: (Long) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(campsites) { campsite ->
            CampsiteCard(
                campsite = campsite,
                onClick = { onCampsiteClick(campsite.id) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}


/**
 * (기존) CampsiteCard Composable (변경 없음)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampsiteCard(
    campsite: Campsite,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Image(
                painter = rememberAsyncImagePainter(campsite.imageUrl),
                contentDescription = campsite.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = campsite.name ?: "",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = campsite.description?:"",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = String.format("%.1f", campsite.rating),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}