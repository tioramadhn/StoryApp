package com.dicoding.storyapp.di

import android.content.Context
import android.util.Log
import com.dicoding.storyapp.data.UserRepository
import com.dicoding.storyapp.data.local.StoryDatabase
import com.dicoding.storyapp.data.remote.retrofit.ApiConfig

object Injection {
    fun provideRepository(context: Context): UserRepository {
        Log.d("REPOSITORY", context.toString())
        val storyDatabase = StoryDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService()
        return UserRepository.getInstance(storyDatabase, apiService)
    }
}