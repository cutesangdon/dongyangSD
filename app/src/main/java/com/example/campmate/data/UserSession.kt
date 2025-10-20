package com.example.campmate.data

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserSession @Inject constructor(private val tokenManager: TokenManager) {
    fun getUserId(): Long? {
        return tokenManager.getUserId()
    }
}

