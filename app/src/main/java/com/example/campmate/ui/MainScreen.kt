package com.example.campmate.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.campmate.ui.checklist.ChecklistDialog
import com.example.campmate.ui.community.CommunityScreen
import com.example.campmate.ui.home.HomeScreen
import com.example.campmate.ui.mypage.MyPageScreen
import com.example.campmate.ui.mypage.ReservationListScreen
import com.example.campmate.ui.navigation.BottomNavItem
import com.example.campmate.ui.weather.WeatherScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onNavigateToDetail: (Int) -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToMyReviews: () -> Unit,
    onNavigateToWriteReview: (Int, String) -> Unit,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    var showChecklistDialog by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = { BottomNavigation(navController = navController) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showChecklistDialog = true }) {
                Icon(Icons.Default.Checklist, contentDescription = "Open Checklist")
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavigationGraph(
                mainNavController = navController,
                onNavigateToDetail = onNavigateToDetail,
                onNavigateToSearch = onNavigateToSearch,
                onNavigateToMyReviews = onNavigateToMyReviews,
                onNavigateToWriteReview = onNavigateToWriteReview,
                onLogout = onLogout
            )
        }
    }

    if (showChecklistDialog) {
        ChecklistDialog(onDismiss = { showChecklistDialog = false })
    }
}

@Composable
fun BottomNavigation(navController: NavHostController) {
    // ✅ [수정] items 리스트에 날씨와 커뮤니티를 추가합니다.
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Weather,
        BottomNavItem.Community,
        BottomNavItem.MyPage
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = stringResource(item.titleId)) },
                label = { Text(stringResource(item.titleId)) },
                selected = currentRoute == item.screenRoute,
                onClick = {
                    navController.navigate(item.screenRoute) {
                        navController.graph.startDestinationRoute?.let {
                            popUpTo(it) { saveState = true }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationGraph(
    mainNavController: NavHostController,
    onNavigateToDetail: (Int) -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToMyReviews: () -> Unit,
    onNavigateToWriteReview: (Int, String) -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            val navBackStackEntry by mainNavController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            val currentScreen = listOf(
                BottomNavItem.Home, BottomNavItem.Weather, BottomNavItem.Community, BottomNavItem.MyPage
            ).find { it.screenRoute == currentRoute }

            if (currentRoute != "reservation_list" && currentRoute != "my_reviews") {
                TopAppBar(
                    title = {
                        Text(text = currentScreen?.titleId?.let { stringResource(it) } ?: "CampMate")
                    },
                    actions = {
                        // 홈 화면에서만 검색 아이콘이 보이도록 수정
                        if (currentRoute == BottomNavItem.Home.screenRoute) {
                            IconButton(onClick = onNavigateToSearch) {
                                Icon(Icons.Default.Search, contentDescription = "Search")
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    )
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = mainNavController,
            startDestination = BottomNavItem.Home.screenRoute,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Home.screenRoute) {
                HomeScreen(onCampsiteClick = onNavigateToDetail)
            }
            composable(BottomNavItem.Weather.screenRoute) {
                WeatherScreen()
            }
            composable(BottomNavItem.Community.screenRoute) {
                CommunityScreen()
            }
            composable(BottomNavItem.MyPage.screenRoute) {
                MyPageScreen(
                    navController = mainNavController,
                    onNavigateToMyReviews = onNavigateToMyReviews,
                    onLogout = onLogout
                )
            }
            composable("reservation_list") {
                ReservationListScreen(
                    onNavigateToWriteReview = onNavigateToWriteReview,
                    onNavigateUp = { mainNavController.popBackStack() }
                )
            }
        }
    }
}
