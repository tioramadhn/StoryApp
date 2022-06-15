package com.dicoding.storyapp.data.remote.retrofit

import com.dicoding.storyapp.data.remote.response.CreateResponse
import com.dicoding.storyapp.data.remote.response.LoginResponse
import com.dicoding.storyapp.data.remote.response.StoriesResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("v1/register")
    suspend fun registUser(
        @Field("name") name: String?,
        @Field("email") email: String?,
        @Field("password") password: String?
    ): CreateResponse

    @FormUrlEncoded
    @POST("v1/login")
    suspend fun loginUser(
        @Field("email") email: String?,
        @Field("password") password: String?
    ): LoginResponse

    @Multipart
    @POST("v1/stories")
    suspend fun addNewStory(
        @Header("Authorization") Auth: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): CreateResponse

    @GET("v1/stories")
    suspend fun getStory(
        @Header("Authorization") Auth: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): StoriesResponse


    @GET("v1/stories?page=1&size=100&location=1")
    suspend fun getStoryByLocation(
        @Header("Authorization") Auth: String,
    ): StoriesResponse
}