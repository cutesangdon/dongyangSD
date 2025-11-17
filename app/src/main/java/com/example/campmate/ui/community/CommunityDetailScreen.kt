package com.example.campmate.ui.community

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.campmate.data.model.CommentResponse
import com.example.campmate.data.model.CommunityDetailResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityDetailScreen(
    onNavigateUp: () -> Unit,
    viewModel: CommunityDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val newComment by viewModel.newComment.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("게시글") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "뒤로가기")
                    }
                }
            )
        },
        bottomBar = {
            CommentInputBar(
                value = newComment,
                onValueChange = viewModel::onNewCommentChange,
                onSendClick = { viewModel.submitComment() }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Scaffold가 제공하는 내부 패딩 적용
        ) {
            when (val state = uiState) {
                is PostDetailUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is PostDetailUiState.Error -> {
                    Text(text = state.message, modifier = Modifier.align(Alignment.Center))
                }
                is PostDetailUiState.Success -> {
                    PostDetailContent(post = state.post)
                }
            }
        }
    }
}

@Composable
fun PostDetailContent(post: CommunityDetailResponse) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        // 1. 게시글 내용
        item {
            Text(text = post.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "작성자: ${post.authorNickname}", style = MaterialTheme.typography.bodySmall)
            // TODO: 날짜 포맷팅 (예: post.createdDate.toLocalDate().toString())
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = post.content, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(32.dp))
            Text("댓글 ${post.comments.size}개", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
        }

        // 2. 댓글 목록
        items(post.comments) { comment ->
            CommentItem(comment = comment)
        }
    }
}

@Composable
fun CommentItem(comment: CommentResponse) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Text(text = comment.authorNickname, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = comment.comment, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun CommentInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp // 하단 입력창 그림자
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                label = { Text("댓글 입력...") },
                modifier = Modifier.weight(1f),
                maxLines = 3
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = onSendClick, enabled = value.isNotBlank()) {
                Icon(Icons.Default.Send, contentDescription = "댓글 전송")
            }
        }
    }
}