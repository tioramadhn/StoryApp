package com.dicoding.storyapp.ui

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.storyapp.data.UserRepository
import com.dicoding.storyapp.data.model.StoryModel
import com.dicoding.storyapp.data.model.UserModel
import com.dicoding.storyapp.data.remote.response.ListStoryItem

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {

    val currentToken = MutableLiveData<String>()

    val story = currentToken.switchMap {
        userRepository.getStory(it).cachedIn(viewModelScope)
    }

    fun getStoryByLocation(token: String) = userRepository.getStoryByLocation(token)

    fun registUser(user: UserModel) = userRepository.registUser(user)

    fun loginUser(user: UserModel) = userRepository.loginUser(user)

    fun addNewStory(story: StoryModel, token: String, isFromCam: Boolean) = userRepository.addNewStory(story, token, isFromCam)
}