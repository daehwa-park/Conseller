package com.example.project.sharedpreferences

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject

class SharedPreferencesUtil @Inject constructor(private val context: Context) {

    companion object {
        private const val PREF_NAME = "my_pref"
        private const val IS_LOGGED_IN = "is_logged_in"
        private const val USER_IDX = "user_idx"
        private const val USER_NICKNAME = "user_nickname"
        private const val USER_TOKEN = "user_token"
    }

    private val sharedPreferences: SharedPreferences
        get() = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    // 로그인 상태를 저장하는 메서드
    fun setLoggedInStatus(isLoggedIn: Boolean) {
        with(sharedPreferences.edit()) {
            putBoolean(IS_LOGGED_IN, isLoggedIn)
            apply()
        }
    }

    // 현재 로그인 상태를 확인하는 메서드
    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(IS_LOGGED_IN, false)
    }

    // 사용자 아이디를 저장하는 메서드
    fun setUserId(useridx: String) {
        with(sharedPreferences.edit()) {
            putString(USER_IDX, useridx)
            apply()
        }
    }

    // 저장된 사용자 아이디를 가져오는 메서드
    fun getUserId(): String? {
        return sharedPreferences.getString(USER_IDX, null)
    }

    // 사용자 닉네임을 저장하는 메서드
    fun setUserNickname(nickname: String) {
        with(sharedPreferences.edit()) {
            putString(USER_NICKNAME, nickname)
            apply()
        }
    }

    // 저장된 사용자 닉네임을 가져오는 메서드
    fun getUserNickname(): String? {
        return sharedPreferences.getString(USER_NICKNAME, null)
    }

    // 사용자 토큰을 저장하는 메서드
    fun setUserToken(token: String) {
        with(sharedPreferences.edit()) {
            putString(USER_TOKEN, token)
            apply()
        }
    }

    // 저장된 사용자 토큰을 가져오는 메서드
    fun getUserToken(): String? {
        return sharedPreferences.getString(USER_TOKEN, null)
    }
}