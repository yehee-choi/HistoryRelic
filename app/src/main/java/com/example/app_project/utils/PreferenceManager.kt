package com.example.app_project.utils

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {

    private val sharedPrefs: SharedPreferences =
        context.getSharedPreferences("auth", Context.MODE_PRIVATE)

    fun saveJWTToken(token: String) {
        sharedPrefs.edit().putString("jwt_token", token).apply()
    }

    fun getJWTToken(): String? {
        return sharedPrefs.getString("jwt_token", null)
    }

    fun isLoggedIn(): Boolean {
        return getJWTToken() != null
    }

    fun clearToken() {
        sharedPrefs.edit().clear().apply()
    }

    fun saveUserInfo(email: String, name: String) {
        sharedPrefs.edit()
            .putString("user_email", email)
            .putString("user_name", name)
            .apply()
    }

    fun getUserEmail(): String? {
        return sharedPrefs.getString("user_email", null)
    }

    fun getUserName(): String? {
        return sharedPrefs.getString("user_name", null)
    }
}