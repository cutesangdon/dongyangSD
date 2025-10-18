package com.example.campmate.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.campmate.R

sealed class BottomNavItem(
    @StringRes val titleId: Int,
    val icon: ImageVector,
    val screenRoute: String
) {
    object Home : BottomNavItem(R.string.bottom_nav_home, Icons.Filled.Home, "home_screen")
    object Weather : BottomNavItem(R.string.bottom_nav_weather,Icons.Filled.WbSunny, "weather_screen")
    object Community : BottomNavItem( R.string.bottom_nav_community,Icons.Filled.Forum, "community_screen")
    object MyPage : BottomNavItem(R.string.bottom_nav_mypage, Icons.Filled.Person, "mypage_screen")
}