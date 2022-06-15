package com.dicoding.storyapp.data

import android.text.TextUtils
import android.util.Patterns
import com.dicoding.storyapp.DataDummy
import com.dicoding.storyapp.data.remote.response.CreateResponse
import com.dicoding.storyapp.data.remote.response.LoginResponse
import com.dicoding.storyapp.data.remote.response.StoriesResponse
import com.dicoding.storyapp.data.remote.retrofit.ApiService
import com.dicoding.storyapp.utils.isValidEmail
import okhttp3.MultipartBody
import okhttp3.RequestBody

class FakeApiService: ApiService {

    private fun isValidEmail(string: String) =
        !TextUtils.isEmpty(string) && Patterns.EMAIL_ADDRESS.matcher(string).matches()

    override suspend fun registUser(
        name: String?,
        email: String?,
        password: String?
    ): CreateResponse {
        return when{
            name.isNullOrBlank() || email.isNullOrBlank() || password.isNullOrBlank() -> {
                CreateResponse(error = true, message = "failed" )
            }
           password.length < 6 -> {
                CreateResponse(error = true, message = "failed" )
            }
            else -> DataDummy.generateDummyCreateResponse()
        }


    }

    override suspend fun loginUser(email: String?, password: String?): LoginResponse {
        return when{
            email.isNullOrBlank() || password.isNullOrBlank() -> {
                DataDummy.generateDummyLoginResponseFail()
            }
            password.length < 6 -> {
                DataDummy.generateDummyLoginResponseFail()
            }
            else -> DataDummy.generateDummyLoginResponseSuccess()
        }
    }

    override suspend fun addNewStory(
        Auth: String,
        file: MultipartBody.Part,
        description: RequestBody
    ): CreateResponse {
        return when{
            Auth.isEmpty() ->  CreateResponse(true, "fail")
            else -> DataDummy.generateDummyCreateResponse()
        }
    }


    override suspend fun getStory(Auth: String, page: Int, size: Int): StoriesResponse {
        return DataDummy.generateDummyStoriesResponse()
    }

    override suspend fun getStoryByLocation(Auth: String): StoriesResponse {
        return DataDummy.generateDummyStoriesResponse()
    }
}