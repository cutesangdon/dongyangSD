package com.example.campmate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.campmate.data.TokenManager
import com.example.campmate.ui.MainScreen
import com.example.campmate.ui.auth.LoginScreen
import com.example.campmate.ui.auth.SignupScreen
import com.example.campmate.ui.detail.CampsiteDetailScreen
import com.example.campmate.ui.mypage.MyReviewsScreen
import com.example.campmate.ui.review.WriteReviewScreen
import com.example.campmate.ui.search.SearchScreen
import com.example.campmate.ui.theme.CampMateTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val startDestination = if (tokenManager.getToken() != null) "main" else "login"

        setContent {
            CampMateTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    CampMateAppNavHost(startDestination = startDestination)
                }
            }
        }
    }
}

@Composable
fun CampMateAppNavHost(startDestination: String) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("main") { popUpTo("login") { inclusive = true } }
                },
                onNavigateToSignup = { navController.navigate("signup") }
            )
        }
        composable("signup") {
            SignupScreen(onSignupSuccess = { navController.popBackStack() })
        }
        composable("main") {
            MainScreen(
                onNavigateToDetail = { campsiteId ->
                    navController.navigate("detail/$campsiteId")
                },
                onNavigateToSearch = {
                    navController.navigate("search")
                },
                onNavigateToMyReviews = {
                    navController.navigate("my_reviews")
                },
                onNavigateToWriteReview = { campsiteId, campsiteName ->
                    navController.navigate("write_review/$campsiteId/$campsiteName")
                },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }
        composable(
            route = "detail/{campsiteId}",
            arguments = listOf(navArgument("campsiteId") { type = NavType.IntType })
        ) {
            CampsiteDetailScreen(onNavigateUp = { navController.popBackStack() })
        }
        composable("search") {
            SearchScreen(
                onNavigateUp = { navController.popBackStack() },
                onNavigateToDetail = { campsiteId ->
                    navController.navigate("detail/$campsiteId")
                }
            )
        }
        composable(
            route = "write_review/{campsiteId}/{campsiteName}",
            arguments = listOf(
                navArgument("campsiteId") { type = NavType.IntType },
                navArgument("campsiteName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("campsiteId") ?: 0
            val name = backStackEntry.arguments?.getString("campsiteName") ?: ""
            WriteReviewScreen(
                campsiteId = id,
                campsiteName = name,
                onNavigateUp = { navController.popBackStack() }
            )
        }
        composable("my_reviews") {
            MyReviewsScreen(onNavigateUp = { navController.popBackStack() })
        }
    }
}