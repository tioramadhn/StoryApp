package com.dicoding.storyapp.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserModel(
    var name:  String? = null,
    var email:  String? = null,
    var password: String? = null,
    var photo: String? = null,
    var description: String? = null,
    var createdAt: String? = null,
    var token: String? = null,
    var isLogin: Boolean = false
): Parcelable