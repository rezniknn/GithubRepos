package com.alexeyreznik.githubrepos.utils

import android.content.Context

/**
 * Created by alexeyreznik on 23/01/2018.
 */
class SharedPrefs(private val context: Context) {

    fun putString(key: String, value: String) {
        val editor = context.getSharedPreferences(SP_NAME, MODE).edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getString(key: String, defaultValue: String): String {
        val sharedPrefs = context.getSharedPreferences(SP_NAME, MODE)
        return sharedPrefs.getString(key, defaultValue)
    }

    companion object {
        const val SP_NAME = "shared_prefs"
        const val MODE = Context.MODE_PRIVATE

        const val KEY_USERNAME = "username"
    }
}