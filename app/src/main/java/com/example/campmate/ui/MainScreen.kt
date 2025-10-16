package com.example.campmate.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.campmate.ui.checklist.ChecklistScreen
import com.example.campmate.ui.home.HomeScreen
import com.example.campmate.ui.mypage.MyPageScreen
import com.example.campmate.ui.mypage.ReservationListScreen
import com.example.campmate.ui.navigation.BottomNavItem

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
    Scaffold(
        bottomBar = { BottomNavigation(navController = navController) }
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
}

@Composable
fun BottomNavigation(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Checklist,
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
                BottomNavItem.Home, BottomNavItem.Checklist, BottomNavItem.MyPage
            ).find { it.screenRoute == currentRoute }

            if (currentRoute != "reservation_list" && currentRoute != "my_reviews") {
                TopAppBar(
                    title = {
                        Text(text = currentScreen?.titleId?.let { stringResource(it) } ?: "CampMate")
                    },
                    actions = {
                        IconButton(onClick = onNavigateToSearch) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
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
            composable(BottomNavItem.Checklist.screenRoute) {
                ChecklistScreen()
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