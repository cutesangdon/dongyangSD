// SplashActivity.kt

package com.example.campmate

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.campmate.ui.SplashScreenContent
import com.example.campmate.ui.theme.CampmateTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. 스플래시 스크린을 설치합니다. (setContent 앞에 와야 합니다)
        installSplashScreen()

        // 2. 스플래시 화면 UI를 설정합니다.
        setContent {
            CampmateTheme {
                SplashScreenContent()
            }
        }

        // 3. 2초 후에 메인 화면으로 이동합니다.
        lifecycleScope.launch {
            delay(2000) // 2초 대기

            val intent = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(intent)
            finish() // 스플래시 화면을 종료하여 뒤로가기로 돌아올 수 없게 함
        }
    }
}