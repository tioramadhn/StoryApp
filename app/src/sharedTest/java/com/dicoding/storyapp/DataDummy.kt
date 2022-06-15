package com.dicoding.storyapp

import android.net.Uri
import android.net.Uri.parse
import android.text.TextUtils
import android.util.Patterns
import com.dicoding.storyapp.data.model.StoryModel
import com.dicoding.storyapp.data.model.UserModel
import com.dicoding.storyapp.data.remote.response.*
import com.dicoding.storyapp.utils.bitmapToFile
import com.dicoding.storyapp.utils.uriToBitmap
import com.dicoding.storyapp.utils.uriToFile
import java.io.File
import java.net.URI

object DataDummy {

    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                id = i.toString(),
                photoUrl = "phohto $i",
                createdAt = "create $i",
                description = "description $i",
                name = "person $i",
                lat = 0.0,
                lon = 0.0
            )
            items.add(story)
        }
        return items
    }

    fun generateDummyStoriesResponse(): StoriesResponse{
        return StoriesResponse(
            this.generateDummyStoryResponse(),
            false,
            "Success"
        )
    }
    fun generateDummyLoginResponse(): LoginResponse{
        return LoginResponse(LoginResult("Tio Ramadhan", "0901", "sdasdasdas.asda.dasda"),false, "Login Success")
    }

    fun generateDummyLoginResponseSuccess(): LoginResponse{
        return LoginResponse(LoginResult("Tio Ramadhan", "0901", "sdasdasdas.asda.dasda"),false, "Login Success")
    }

    fun generateDummyLoginResponseFail(): LoginResponse{
        return LoginResponse(LoginResult("Tio Ramadhan", "0901", "sdasdasdas.asda.dasda"),true, "Login Failed")
    }


    fun generateDummyCreateResponse(): CreateResponse{
        return CreateResponse(error = false, message = "success")
    }

    fun generateDummyStory(): StoryModel{
        return StoryModel(File("photo"), "ini adalah gambar")
    }
}