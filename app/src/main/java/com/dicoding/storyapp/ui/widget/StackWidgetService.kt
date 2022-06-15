package com.dicoding.storyapp.ui.widget

import android.content.Intent
import android.util.Log
import android.widget.RemoteViewsService
import android.widget.Toast
import com.dicoding.storyapp.data.local.UserPreference
import com.dicoding.storyapp.data.remote.response.ListStoryItem
import com.dicoding.storyapp.data.remote.response.StoriesResponse
import com.dicoding.storyapp.data.remote.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StackWidgetService: RemoteViewsService() {

    override fun onGetViewFactory(p0: Intent): RemoteViewsFactory {
        return StackRemoteViewsFactory(this.applicationContext)
    }
}