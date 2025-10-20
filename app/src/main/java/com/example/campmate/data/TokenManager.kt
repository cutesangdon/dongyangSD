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
        private const val USER_NUMERIC_ID = "user_numeric_id"
    }

    fun saveAuthData(token: String, userId: Long) {
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, token)
        editor.putLong(USER_NUMERIC_ID, userId)
        editor.apply()
    }

    fun getToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }

    fun getUserId(): Long {
        // ID가 없을 경우 -1을 반환 (오류 식별용)
        return prefs.getLong(USER_NUMERIC_ID, -1L)
    }

    fun clearAuthData() {
        val editor = prefs.edit()
        editor.remove(USER_TOKEN)
        editor.remove(USER_NUMERIC_ID)
        editor.apply()
    }
}

