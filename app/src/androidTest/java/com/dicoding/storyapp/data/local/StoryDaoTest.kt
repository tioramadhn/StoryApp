package com.dicoding.storyapp.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.dicoding.storyapp.DataDummy
import com.dicoding.storyapp.getOrAwaitValue
import kotlinx.coroutines.test.runBlockingTest
import org.junit.*
import org.junit.Assert.*

class StoryDaoTest{
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    private lateinit var database: StoryDatabase
    private lateinit var dao: StoryDao
    private val sampleStory = DataDummy.generateDummyStoryResponse()

    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            StoryDatabase::class.java
        ).build()
        dao = database.storyDao()
    }

    @After
    fun closeDb() = database.close()


    @Test
    fun saveStory() = runBlockingTest {
        dao.insertStory(sampleStory)
        val actualStory = dao.getStory().getOrAwaitValue()
        Assert.assertNotNull(actualStory)
        Assert.assertEquals(sampleStory[0].id, actualStory[0].id)
        Assert.assertEquals(sampleStory.size, actualStory.size)
    }

    @Test
    fun deleteStory() = runBlockingTest {
        dao.insertStory(sampleStory)
        dao.deleteAll()
        val actualStory = dao.getStory().getOrAwaitValue()
        Assert.assertTrue(actualStory.isEmpty())
    }
}
