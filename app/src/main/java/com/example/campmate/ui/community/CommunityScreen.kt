package com.example.campmate.ui.community

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.campmate.data.model.CommunitySummaryResponse

@Composable
fun CommunityScreen(
    onNavigateToWritePost: () -> Unit,
    onNavigateToPostDetail: (Long) -> Unit,
    viewModel: CommunityViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // ✅✅✅ [핵심 수정] ✅✅✅
    // 화면의 생명주기(Lifecycle)를 관찰합니다.
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            // 화면이 다시 활성화될 때 (글쓰기 후 돌아올 때 등)
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadPosts() // 게시글 목록을 새로고침합니다.
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        // 화면이 사라질 때 관찰자를 제거합니다.
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToWritePost) {
                Icon(Icons.Default.Add, contentDescription = "글쓰기")
            }
        },
        floatingActionButtonPosition = FabPosition.Start
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (val state = uiState) {
                is CommunityUiState.Loading -> {
                    CircularProgressIndicator()
                }
                is CommunityUiState.Error -> {
                    Text(text = state.message)
                }
                is CommunityUiState.Success -> {
                    if (state.posts.isEmpty()) {
                        Text(text = "현재 등록된 게시글이 없습니다!")
                    } else {
                        PostList(posts = state.posts, onPostClick = onNavigateToPostDetail)
                    }
                }
            }
        }
    }
}

@Composable
fun PostList(posts: List<CommunitySummaryResponse>, onPostClick: (Long) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp) // 카드 간격 8.dp
    ) {
        items(posts) { post ->
            PostCard(post = post, onClick = { onPostClick(post.id) })
        }
    }
}

@Composable
fun PostCard(post: CommunitySummaryResponse, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) { // 내부 여백 12.dp
            Text(text = post.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) // 글꼴 크기 줄임
            Spacer(modifier = Modifier.height(4.dp)) // 간격 줄임
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = post.authorNickname, style = MaterialTheme.typography.bodyMedium)
                Text(text = "댓글 ${post.commentCount}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}