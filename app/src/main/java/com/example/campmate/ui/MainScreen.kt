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
import androidx.compose.material3.NavigationBarItemDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToMyReviews: () -> Unit,
    onNavigateToWriteReview: (Long, Int, String) -> Unit,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    var showChecklistDialog by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            val showTopBar = currentRoute != "reservation_list" && currentRoute != "my_reviews"

            if (showTopBar) {
                TopAppBar(
                    title = {
                        Text(
                            text = "CampMate",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    actions = {
                        if (currentRoute == BottomNavItem.Home.screenRoute) {
                            IconButton(onClick = onNavigateToSearch) {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    )
                )
            }
        },
        bottomBar = {
            BottomNavigation(
                navController = navController,
                containerColor = MaterialTheme.colorScheme.primary,
                selectedColor = MaterialTheme.colorScheme.onPrimary,
                unselectedColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f)
            )
        },
        floatingActionButton = {
            // (수정) FloatingActionButton의 색상을 primary/onPrimary로 강제 지정
            FloatingActionButton(
                onClick = { showChecklistDialog = true },
                containerColor = MaterialTheme.colorScheme.primary, // 배경: 어두운 녹색
                contentColor = MaterialTheme.colorScheme.onPrimary    // 아이콘: 흰색
            ) {
                Icon(Icons.Default.Checklist, contentDescription = "Open Checklist")
            }
        }
    ) { innerPadding ->

        NavigationGraph(
            mainNavController = navController,
            onNavigateToDetail = onNavigateToDetail,
            onNavigateToSearch = onNavigateToSearch,
            onNavigateToMyReviews = onNavigateToMyReviews,
            onNavigateToWriteReview = onNavigateToWriteReview,
            onLogout = onLogout,
            modifier = Modifier.padding(innerPadding)
        )
    }

    if (showChecklistDialog) {
        ChecklistDialog(onDismiss = { showChecklistDialog = false })
    }
}

@Composable
fun BottomNavigation(
    navController: NavHostController,
    containerColor: Color,
    selectedColor: Color,
    unselectedColor: Color
) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Weather,
        BottomNavItem.Community,
        BottomNavItem.MyPage
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(containerColor = containerColor) {
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
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = selectedColor,
                    selectedTextColor = selectedColor,
                    unselectedIconColor = unselectedColor,
                    unselectedTextColor = unselectedColor
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationGraph(
    mainNavController: NavHostController,
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToMyReviews: () -> Unit,
    onNavigateToWriteReview: (Long, Int, String) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = mainNavController,
        startDestination = BottomNavItem.Home.screenRoute,
        modifier = modifier
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
                //25.11.10 KM수정 리뷰
                //onNavigateToWriteReview = onNavigateToWriteReview,
                onNavigateToWriteReview = { reservationId, campsiteId, campsiteName ->
                    onNavigateToWriteReview( reservationId, campsiteId, campsiteName)
                },
                onNavigateUp = { mainNavController.popBackStack() }
            )
        }
    }
}