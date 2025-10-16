package com.example.campmate.data

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(@ApplicationContext context: Context) {
    // 'campmate_prefs'라는 이름의 안전한 저장소를 만듭니다.
    private val prefs: SharedPreferences = context.getSharedPreferences("campmate_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val USER_TOKEN = "user_token"
    }

    // 토큰을 저장하는 함수
    fun saveToken(token: String) {
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, token)
        editor.apply()
    }

    // 저장된 토큰을 꺼내는 함수
    fun getToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }

    /**
     * ✅✅✅ [이 함수 추가] 저장된 토큰을 삭제하는 함수입니다. ✅✅✅
     * 로그아웃 시 호출됩니다.
     */
    fun clearToken() {
        val editor = prefs.edit()
        editor.remove(USER_TOKEN)
        editor.apply()
    }
}