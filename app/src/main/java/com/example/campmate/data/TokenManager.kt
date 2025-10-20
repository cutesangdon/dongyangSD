package com.example.campmate.data

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(@ApplicationContext context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("campmate_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val USER_TOKEN = "user_token"
        private const val USER_ID = "user_id" // 사용자 숫자 ID를 저장할 키
    }

    fun saveAuthData(token: String, userId: Long) {
        prefs.edit()
            .putString(USER_TOKEN, token)
            .putLong(USER_ID, userId)
            .apply()
    }

    fun getToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }

    fun getUserId(): Long? {
        val id = prefs.getLong(USER_ID, -1L)
        return if (id == -1L) null else id
    }

    fun clearAuthData() {
        prefs.edit()
            .remove(USER_TOKEN)
            .remove(USER_ID)
            .apply()
    }
}

