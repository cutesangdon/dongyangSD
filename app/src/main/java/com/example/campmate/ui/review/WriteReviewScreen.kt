package com.example.campmate.ui.review


// 25.11.10 KM 추가
import androidx.compose.runtime.LaunchedEffect // 추가
import kotlinx.coroutines.flow.collectLatest //추가
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.AddAPhoto
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WriteReviewScreen(
    // 11.10 KM 수정
    reservationId: Long, //추가
    campsiteId: Int,
    campsiteName: String,
    onNavigateUp: () -> Unit,
    viewModel: WriteReviewViewModel = hiltViewModel()
) {
    var rating by remember { mutableStateOf(0) }
    var content by remember { mutableStateOf("") }
    val context = LocalContext.current

    // 25.11.10 LaunchedEffect: ViewModel의 submissionResult를 구독하여 API 호출 결과를 기다립니다. 리뷰
    LaunchedEffect(Unit) {
        viewModel.submissionResult.collectLatest { success ->
            if (success) {
                Toast.makeText(context, "리뷰가 등록되었습니다!", Toast.LENGTH_SHORT).show()
                onNavigateUp() // ✅ API 성공 후 화면 닫기 (요청 취소 방지)
            } else {
                Toast.makeText(context, "리뷰 등록에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("리뷰 작성") },
                navigationIcon = { IconButton(onClick = onNavigateUp) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).padding(16.dp).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("'$campsiteName'은(는) 어떠셨나요?", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))
            StarRatingInput(rating = rating, onRatingChange = { rating = it })
            Spacer(Modifier.height(24.dp))
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                modifier = Modifier.fillMaxWidth().height(200.dp),
                label = { Text("캠핑 경험을 공유해주세요.") }
            )
            Spacer(Modifier.height(16.dp))
            OutlinedButton(onClick = { /* TODO: 갤러리 열기 기능 구현 */ }) {
                Icon(Icons.Outlined.AddAPhoto, contentDescription = "Add Photo")
                Spacer(Modifier.width(8.dp))
                Text("사진 추가")
            }
            Spacer(Modifier.weight(1f))
            Button(
                onClick = {
                    // ✅ ViewModel의 submitReview 함수를 호출할 때 Int 타입인 campsiteId를 전달합니다.
                    viewModel.submitReview(reservationId,campsiteId, rating, content)
                    Toast.makeText(context, "리뷰가 등록되었습니다!", Toast.LENGTH_SHORT).show()
                    onNavigateUp()
                },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("등록하기")
            }
        }
    }
}

@Composable
fun StarRatingInput(rating: Int, onRatingChange: (Int) -> Unit) {
    Row(horizontalArrangement = Arrangement.Center) {
        for (i in 1..5) {
            IconButton(onClick = { onRatingChange(i) }) {
                Icon(
                    imageVector = if (i <= rating) Icons.Filled.Star else Icons.Outlined.StarOutline,
                    contentDescription = "Star $i",
                    tint = if (i <= rating) Color(0xFFFFC107) else Color.Gray,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }
}