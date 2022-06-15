package com.dicoding.storyapp.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import androidx.recyclerview.widget.ListUpdateCallback
import com.dicoding.storyapp.DataDummy
import com.dicoding.storyapp.MainCoroutineRule
import com.dicoding.storyapp.adapter.StoryListAdapter
import com.dicoding.storyapp.data.remote.response.ListStoryItem
import com.dicoding.storyapp.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import  com.dicoding.storyapp.data.Status
import com.dicoding.storyapp.data.UserRepository
import com.dicoding.storyapp.data.model.StoryModel
import com.dicoding.storyapp.data.model.UserModel
import com.dicoding.storyapp.data.remote.response.CreateResponse
import com.dicoding.storyapp.data.remote.response.LoginResponse
import com.dicoding.storyapp.utils.uriToFile
import org.junit.Before
import org.mockito.Mockito.`when`
import java.io.File

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class UserViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRules = MainCoroutineRule()

    @Mock
    private lateinit var userRepositoryMock: UserRepository


    @Mock
    private lateinit var userViewModelMock: UserViewModel

    private lateinit var userViewModel: UserViewModel
    private val dummyLogin = DataDummy.generateDummyLoginResponse()

    @Before
    fun setUp() {
        userViewModel = UserViewModel(userRepositoryMock)
    }

    @Test
    fun `When Login Valid Should Not Null and Return Success`() {
        val expectedUser = MutableLiveData<Status<LoginResponse>>()
        val validUser = UserModel(email = "tioramadhan9f@gmail.com", password = "Dicoding")
        expectedUser.value = Status.Success(dummyLogin)
        `when`(userViewModel.loginUser(validUser)).thenReturn(expectedUser)
        val actual = userViewModel.loginUser(validUser).getOrAwaitValue()
        Mockito.verify(userRepositoryMock).loginUser(validUser)
        Assert.assertNotNull(actual)
        Assert.assertTrue(actual is Status.Success)
    }

    @Test
    fun `When Login InValid Should Return Error`() {
        val expectedUser = MutableLiveData<Status<LoginResponse>>()
        val invalidUser = UserModel(email = "tioramadhan9f@gmail.com", password = "123")
        expectedUser.value = Status.Error(dummyLogin.message)
        `when`(userViewModel.loginUser(invalidUser)).thenReturn(expectedUser)
        val actual = userViewModel.loginUser(invalidUser).getOrAwaitValue()
        Mockito.verify(userRepositoryMock).loginUser(invalidUser)
        Assert.assertTrue(actual is Status.Error)
    }

    @Test
    fun `when Get Story Should Not Null`() = mainCoroutineRules.runBlockingTest {
        val dummyStory = DataDummy.generateDummyStoryResponse()
        val data = PagedTestDataSources.snapshot(dummyStory)
        val story = MutableLiveData<PagingData<ListStoryItem>>()
        story.value = data
        `when`(userViewModelMock.story).thenReturn(story)
        val actualNews = userViewModelMock.story.getOrAwaitValue()
        userRepositoryMock.getStory("token")

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryListAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            mainDispatcher = mainCoroutineRules.dispatcher,
            workerDispatcher = mainCoroutineRules.dispatcher,
        )
        differ.submitData(actualNews)

        advanceUntilIdle()

        Mockito.verify(userViewModelMock).story
        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyStory.size, differ.snapshot().size)
        Assert.assertEquals(dummyStory[0].id, differ.snapshot()[0]?.id)

    }


    @Test
    fun `When Registration Valid Should Return Success`() {
        val expectedUser = MutableLiveData<Status<CreateResponse>>()
        val validUser =
            UserModel(name = "Tio Ramadhan", email = "emailgue@gmail.com", password = "12345678")
        expectedUser.value = Status.Success(DataDummy.generateDummyCreateResponse())
        `when`(userViewModel.registUser(validUser)).thenReturn(expectedUser)
        val actual = userViewModel.registUser(validUser).getOrAwaitValue()
        Mockito.verify(userRepositoryMock).registUser(validUser)
        Assert.assertTrue(actual is Status.Success)
    }

    @Test
    fun `When Registration NotValid Should Return Error`() {
        val expectedUser = MutableLiveData<Status<CreateResponse>>()
        val validUser =
            UserModel(name = "Tio Ramadhan", email = "emailgue@gmail.com", password = "12345678")
        expectedUser.value = Status.Error(DataDummy.generateDummyCreateResponse().message)
        `when`(userViewModel.registUser(validUser)).thenReturn(expectedUser)
        val actual = userViewModel.registUser(validUser).getOrAwaitValue()
        Mockito.verify(userRepositoryMock).registUser(validUser)
        Assert.assertTrue(actual is Status.Error)
    }

    @Test
    fun `When Add Story Valid Should Return Success`() {
        val validStory = DataDummy.generateDummyStory()
        val expectedStory = MutableLiveData<Status<CreateResponse>>()
        expectedStory.value = Status.Success(DataDummy.generateDummyCreateResponse())
        `when`(userViewModel.addNewStory(validStory, "token", false)).thenReturn(expectedStory)
        val actual = userViewModel.addNewStory(validStory, "token", false).getOrAwaitValue()
        Mockito.verify(userRepositoryMock).addNewStory(validStory, "token", false)
        Assert.assertTrue(actual is Status.Success)
    }

    @Test
    fun `When Add Story InValid Should Return Error`(){
        val inValidStory = StoryModel(File("file"), "deskripsi")
        val expectedStory = MutableLiveData<Status<CreateResponse>>()
        expectedStory.value = Status.Error("Error")
        `when`(userViewModel.addNewStory(inValidStory, "token", false)).thenReturn(expectedStory)
        val actual = userViewModel.addNewStory(inValidStory, "token", false).getOrAwaitValue()
        Mockito.verify(userRepositoryMock).addNewStory(inValidStory, "token", false)
        Assert.assertTrue(actual is Status.Error)
    }

    @Test
    fun `When Get Story By Location success Should Not Null and Return Success`(){
        val dataDummy = DataDummy.generateDummyStoryResponse()
        val expectedValue = MutableLiveData<Status<List<ListStoryItem>>>()
        expectedValue.value = Status.Success(dataDummy)
        `when`(userViewModel.getStoryByLocation("token")).thenReturn(expectedValue)
        val actual = userViewModel.getStoryByLocation( "token").getOrAwaitValue()
        Mockito.verify(userRepositoryMock).getStoryByLocation( "token")
        Assert.assertTrue(actual is Status.Success)
        Assert.assertNotNull(actual)
        Assert.assertEquals(dataDummy.size,( actual as Status.Success).data.size)
    }

    @Test
    fun `When Get Story By Location failed Should Return Error`(){
        val dataDummy = DataDummy.generateDummyStoryResponse()
        val expectedValue = MutableLiveData<Status<List<ListStoryItem>>>()
        expectedValue.value = Status.Error("Error")
        `when`(userViewModel.getStoryByLocation("token")).thenReturn(expectedValue)
        val actual = userViewModel.getStoryByLocation( "token").getOrAwaitValue()
        Mockito.verify(userRepositoryMock).getStoryByLocation( "token")
        Assert.assertTrue(actual is Status.Error)
        Assert.assertNotNull(actual)
    }

}

class PagedTestDataSources private constructor(private val items: List<ListStoryItem>) :
    PagingSource<Int, LiveData<List<ListStoryItem>>>() {
    companion object {
        fun snapshot(items: List<ListStoryItem>): PagingData<ListStoryItem> {
            return PagingData.from(items)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, LiveData<List<ListStoryItem>>>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<ListStoryItem>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}