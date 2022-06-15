package com.dicoding.storyapp.ui

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.storyapp.R
import com.dicoding.storyapp.adapter.LoadingStateAdapter
import com.dicoding.storyapp.adapter.StoryListAdapter
import com.dicoding.storyapp.data.local.UserPreference
import com.dicoding.storyapp.data.model.UserModel
import com.dicoding.storyapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var mUserPreference: UserPreference
    private lateinit var userModel: UserModel
    private lateinit var binding: ActivityMainBinding
    private val viewModel: UserViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = getString(R.string.app_name)
        mUserPreference = UserPreference(this)

        setupViewModel()
        binding.btnAddStory.setOnClickListener {
            startActivity(Intent(this, AddStoryActivity::class.java))
        }

        checkAuth()
    }

    private fun checkAuth() {
        userModel = mUserPreference.getUser()
        if (!userModel.isLogin || userModel.token?.isBlank() == true) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun setupViewModel() {
        userModel = mUserPreference.getUser()
        userModel.token?.let {
            getData(it)
//
        }

    }

    private fun getData(token: String) {
        val adapter = StoryListAdapter()
        binding.apply {
            rvStory.layoutManager = LinearLayoutManager(this@MainActivity)
            rvStory.adapter = adapter.withLoadStateFooter(
                footer = LoadingStateAdapter {
                    adapter.retry()
                }
            )
        }
        viewModel.currentToken.value = token
        viewModel.story.observe(this) {
            Log.d("paging: submit data", it.toString())
            adapter.submitData(lifecycle, it)
            if (adapter.itemCount == 0) {
                binding.tvError.visibility = View.VISIBLE
            } else {
                binding.tvError.visibility = View.GONE
            }
        }


    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                val mUserPreference = UserPreference(this)
                mUserPreference.logout()
                startActivity(Intent(this, LoginActivity::class.java))
                return true
            }
            R.id.languange -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                return true
            }

            R.id.maps -> {
                startActivity(Intent(this, MapsActivity::class.java))
                return true
            }

            else -> return true
        }
    }
}