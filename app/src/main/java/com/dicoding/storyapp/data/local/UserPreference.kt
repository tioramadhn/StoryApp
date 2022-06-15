package com.dicoding.storyapp.data.local

import android.content.Context
import com.dicoding.storyapp.data.model.UserModel

internal class UserPreference constructor(context: Context) {
    private val preferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
    private val stack = context.getSharedPreferences(STACK, Context.MODE_PRIVATE)

    companion object {
        private const val NAME = "name"
        private const val STACK = "stack"
        const val TOKEN = ""
        private const val IS_LOGIN = "islogin"
    }

    fun setUser(value: UserModel) {
        val editor = preferences.edit()
        editor.putString(NAME, value.name)
        editor.putString(TOKEN, value.token)
        editor.putBoolean(IS_LOGIN, value.isLogin)
        editor.apply()
    }

    fun getUser(): UserModel {
        val model = UserModel()
        model.name = preferences.getString(NAME, "")
        model.token = preferences.getString(TOKEN, "")
        model.isLogin = preferences.getBoolean(IS_LOGIN, false)
        return model
    }

    fun setImage(key: String, photo: String) {
        val editor = stack.edit()
        editor.putString(key, photo)
        editor.apply()
    }

    fun getImage(): MutableMap<String, *>? {
        return stack.all
    }


    fun logout() {
        val stack = stack.edit()
        stack.clear()
        stack.apply()

        val editor = preferences.edit()
        editor.putString(NAME, "")
        editor.putString(TOKEN, "")
        editor.putBoolean(IS_LOGIN, false)
        editor.apply()
    }

}