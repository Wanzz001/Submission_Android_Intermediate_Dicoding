package org.d3if0080.mystoryapp.api.response.upload

import com.google.gson.annotations.SerializedName

data class ResponseUploadStory(
    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)
