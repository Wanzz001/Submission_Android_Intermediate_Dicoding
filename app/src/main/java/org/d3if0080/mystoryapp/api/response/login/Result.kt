package org.d3if0080.mystoryapp.api.response.login

import com.google.gson.annotations.SerializedName

data class Result(
    @field:SerializedName("userId")
    val userId: String,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("token")
    val token: String,
)
