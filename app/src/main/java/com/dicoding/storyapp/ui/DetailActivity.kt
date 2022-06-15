package com.dicoding.storyapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.model.UserModel
import com.dicoding.storyapp.databinding.ActivityDetailBinding
import com.dicoding.storyapp.utils.withDateFormat

class DetailActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_PERSON = "extra"
    }

    private lateinit var binding: ActivityDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = getString(R.string.detail_story)
        val person = intent.getParcelableExtra<UserModel>(EXTRA_PERSON) as UserModel

        populateView(person)
    }

    private fun populateView(person: UserModel) {
        Glide.with(this)
            .load(person.photo.toString())
            .placeholder(R.drawable.placeholder)
            .into(binding.imgPhoto)

        Glide.with(this)
            .load(R.drawable.profile)
            .circleCrop()
            .into(binding.imgUserPhoto)

        person.apply {
            binding.apply {
                tvName.text = name
                tvDate.text = createdAt?.withDateFormat() ?: ""
                tvDescription.text = description
            }
        }

    }
}