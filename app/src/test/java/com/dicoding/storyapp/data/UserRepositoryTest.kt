package com.dicoding.storyapp.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.dicoding.storyapp.DataDummy
import com.dicoding.storyapp.MainCoroutineRule
import com.dicoding.storyapp.data.local.StoryDatabase
import com.dicoding.storyapp.data.remote.retrofit.ApiService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.Assert
import org.junit.Before
import org.junit.Rule

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class UserRepositoryTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRules = MainCoroutineRule()

    @Mock
    private lateinit var storyDatabase: StoryDatabase
    private lateinit var apiService: ApiService
    private lateinit var userRepository: UserRepository

    @Before
    fun setUp() {
        apiService = FakeApiService()
        userRepository = UserRepository(storyDatabase, apiService)
    }

    @Test
    fun `When registUser Valid Should not null and error is false`() = mainCoroutineRules.runBlockingTest {
        val expectedError = false
        val actual = apiService.registUser("tio", "tio@gmail.com", "12345678")
        Assert.assertNotNull(actual)
        Assert.assertEquals(expectedError, actual.error)
    }

    @Test
    fun `When registUser Not Valid Should error is true`() = mainCoroutineRules.runBlockingTest {
        val expectedError = true
        val actual = apiService.registUser("tio", "tio@gmail.com", "")
        Assert.assertNotNull(actual)
        Assert.assertEquals(expectedError, actual.error)
    }


    @Test
    fun `When loginUser Valid Should not null`() = mainCoroutineRules.runBlockingTest {
        val expected = DataDummy.generateDummyLoginResponse()
        val actual = apiService.loginUser("tioramadhan9f@gmail.com", "Dicoding")
        Assert.assertNotNull(actual)
        Assert.assertEquals(expected.error, actual.error)
    }

    @Test
    fun `When loginUser Not Valid Should error`() = mainCoroutineRules.runBlockingTest {
        val expectedError = true
        val actual = apiService.loginUser("tioramadhan9f@gmail.com", "")
        Assert.assertNotNull(actual)
        Assert.assertEquals(expectedError, actual.error)
    }

    @Test
    fun `When getStory Should not Null`() = mainCoroutineRules.runBlockingTest {
        val expected = DataDummy.generateDummyStoriesResponse()
        val actual = apiService.getStory("Dicoding", 1, 5)
        Assert.assertNotNull(actual)
        Assert.assertEquals(expected.listStory.size, actual.listStory.size)
    }

    @Test
    fun `When getStoryByLocation Should Not Null`() = mainCoroutineRules.runBlockingTest {
        val expected = DataDummy.generateDummyStoriesResponse()
        val actual = apiService.getStoryByLocation("token")
        Assert.assertNotNull(actual)
        Assert.assertEquals(expected.listStory.size, actual.listStory.size)
    }

    @Test
    fun `When addNewStory Valid Should error is false`() = mainCoroutineRules.runBlockingTest {
        val file = MultipartBody.Part.createFormData("gambar", "tes")
        val desc = "desckripsi".toRequestBody("text/plain".toMediaType())
        val expected = DataDummy.generateDummyCreateResponse()
        val actual = apiService.addNewStory("tio", file, desc)
        Assert.assertNotNull(actual)
        Assert.assertEquals(expected.error, actual.error)
    }

    @Test
    fun `When addNewStory Not Valid Should error is true`() = mainCoroutineRules.runBlockingTest {
        val file = MultipartBody.Part.createFormData("gambar", "tes")
        val desc = "desckripsi".toRequestBody("text/plain".toMediaType())
        val expected = true
        val actual = apiService.addNewStory("", file, desc)
        Assert.assertNotNull(actual)
        Assert.assertEquals(expected, actual.error)
    }
}