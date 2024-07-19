package com.example.codeopus

import android.content.Context
import android.content.SharedPreferences

object UserSession {
    private const val PREF_NAME = "user_session"
    private const val KEY_USER_ID = "user_id"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveUserId(context: Context, userId: String) {
        val editor = getPreferences(context).edit()
        editor.putString(KEY_USER_ID, userId)
        editor.apply()
    }

    fun getUserId(context: Context): String? {
        return getPreferences(context).getString(KEY_USER_ID, null)
    }

    fun clearSession(context: Context) {
        val editor = getPreferences(context).edit()
        editor.clear()
        editor.apply()
    }
}
