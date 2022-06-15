package com.dicoding.storyapp.data

import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.*
import com.dicoding.storyapp.data.local.StoryDatabase
import com.dicoding.storyapp.data.model.StoryModel
import com.dicoding.storyapp.data.model.UserModel
import com.dicoding.storyapp.data.remote.response.CreateResponse
import com.dicoding.storyapp.data.remote.response.ListStoryItem
import com.dicoding.storyapp.data.remote.response.LoginResponse
import com.dicoding.storyapp.data.remote.retrofit.ApiService
import com.dicoding.storyapp.utils.bitmapToFile
import com.dicoding.storyapp.utils.reduceFileImage
import com.dicoding.storyapp.utils.rotateBitmap
import com.dicoding.storyapp.utils.wrapEspressoIdlingResource
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class UserRepository constructor(
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService
) {
    fun registUser(user: UserModel): LiveData<Status<CreateResponse>> = liveData {
        emit(Status.Loading)
            try {
                val response = apiService.registUser(user.name, user.email, user.password)
                if (!response.error) {
                    emit(Status.Success(response))
                } else {
                    emit(Status.Error(response.message))
                }
            } catch (e: Exception) {
                Log.d("UserRepository", "register user: ${e.message.toString()} ")
                emit(Status.Error(e.message.toString()))
            }
    }

    fun loginUser(user: UserModel): LiveData<Status<LoginResponse>> = liveData {
        emit(Status.Loading)
            try {
                val response = apiService.loginUser(user.email, user.password)
                if (!response.error) {
                    emit(Status.Success(response))
                } else {
                    Log.d("UserRepository", "login user: ${response.message} ")
                    emit(Status.Error(response.message))
                }
            } catch (e: Exception) {
                Log.d("UserRepository", "login user: $e ")
                emit(Status.Error(e.message.toString()))
            }
    }


    fun getStory(token: String): LiveData<PagingData<ListStoryItem>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5,
                enablePlaceholders = false
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService, token),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }

    fun getStoryByLocation(token: String): LiveData<Status<List<ListStoryItem>>> = liveData{
        emit(Status.Loading)
            try {
                val response = apiService.getStoryByLocation("Bearer ${token}")
                if (!response.error) {
                    emit(Status.Success(response.listStory))
                    Log.d(
                        "UserRepository",
                        "get all stor by location: ${response.message.toString()} "
                    )
                    Log.d("UserRepository", "get all stor by location: ${response.listStory} ")
                    Log.d("UserRepository", "get all stor by location: ${response.listStory.size} ")
                } else {
                    emit(Status.NotFound)
                    Log.d(
                        "UserRepository",
                        "get all story by location: ${response.message.toString()} "
                    )
                }
            } catch (e: Exception) {
                Log.d("UserRepository", "get all story by location: ${e.message.toString()} ")
                emit(Status.Error(e.message.toString()))
            }
    }

    fun addNewStory(
        story: StoryModel,
        token: String,
        isFromCam: Boolean
    ): LiveData<Status<CreateResponse>> = liveData {
        emit(Status.Loading)
        var file: File?
        if (isFromCam) {
            Log.d("UserRepository", "isFromCam : true")
            val rotate = rotateBitmap(
                BitmapFactory.decodeFile(story.file.path),
                true
            )
            val result = bitmapToFile(rotate, story.file.path)

            file = result?.let { reduceFileImage(it) }
        } else {
            Log.d("UserRepository", "isFromCam : false")
            file = reduceFileImage(story.file)
        }

        Log.d("UserRepository", "pathreduce : ${file!!.path}")

        val description = story.description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            file.name,
            requestImageFile
        )
            try {
                val response =
                    apiService.addNewStory("Bearer ${token}", imageMultipart, description)
                if (!response.error) {
                    emit(Status.Success(response))
                    Log.d("UserRepository", "add new story: succesfuly")
                } else {
                    emit(Status.Error(response.message))
                    Log.d("UserRepository", "add new story: failed")
                }
            } catch (e: Exception) {
                Log.d("UserRepository", "add new story: ${e.message.toString()} ")
                emit(Status.Error(e.message.toString()))
            }
    }


    companion object {
        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(
            storyDatabase: StoryDatabase,
            apiService: ApiService
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository( storyDatabase, apiService)
            }.also { instance = it }
    }
}