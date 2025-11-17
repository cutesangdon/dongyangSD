package com.example.campmate.ui.community

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WritePostScreen(
    onNavigateUp: () -> Unit,
    viewModel: WritePostViewModel = hiltViewModel()
) {
    val title by viewModel.title.collectAsState()
    val content by viewModel.content.collectAsState()
    val context = LocalContext.current

    // postResult 이벤트를 구독하여 토스트 메시지 표시 및 화면 종료
    LaunchedEffect(Unit) {
        viewModel.postResult.collectLatest { success ->
            if (success) {
                Toast.makeText(context, "게시글이 등록되었습니다.", Toast.LENGTH_SHORT).show()
                onNavigateUp() // 성공 시 뒤로가기
            } else {
                Toast.makeText(context, "등록에 실패했습니다. (내용을 확인해주세요)", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("새 게시글 작성") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "뒤로가기")
                    }
                },
                actions = {
                    TextButton(onClick = { viewModel.submitPost() }) {
                        Text("완료")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = viewModel::onTitleChange,
                label = { Text("제목") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = content,
                onValueChange = viewModel::onContentChange,
                label = { Text("내용") },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // 남은 공간을 모두 차지
            )
        }
    }
}