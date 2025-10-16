package com.example.campmate.ui.mypage

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.campmate.R

@Composable
fun MyPageScreen(
    navController: NavController,
    onNavigateToMyReviews: () -> Unit,
    onLogout: () -> Unit, // 로그아웃 후 화면 이동을 위한 동작
    viewModel: MyPageViewModel = hiltViewModel() // ViewModel 연결
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        MenuItem(
            text = stringResource(R.string.mypage_my_reservations),
            onClick = { navController.navigate("reservation_list") }
        )
        MenuItem(
            text = stringResource(R.string.mypage_my_reviews),
            onClick = onNavigateToMyReviews
        )
        // ✅ 로그아웃 메뉴 추가
        MenuItem(
            text = stringResource(R.string.mypage_logout),
            onClick = {
                viewModel.logout() // 1. 토큰 삭제
                onLogout()       // 2. 로그인 화면으로 이동
            }
        )
    }
}

@Composable
fun MenuItem(text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null
        )
    }
    Divider()
}