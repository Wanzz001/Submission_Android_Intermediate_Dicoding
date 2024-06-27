package org.d3if0080.mystoryapp.api.services

import org.d3if0080.mystoryapp.api.response.home.ResponseHome
import org.d3if0080.mystoryapp.api.response.login.ResponseLogin
import org.d3if0080.mystoryapp.api.response.register.ResponseRegister
import org.d3if0080.mystoryapp.api.response.upload.ResponseUploadStory
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {

    @POST("register")
    @FormUrlEncoded
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ) : ResponseRegister

    @POST("login")
    @FormUrlEncoded
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ) : ResponseLogin

    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") auth: String
    ) : ResponseHome

    @Multipart
    @POST("stories")
    suspend fun uploadStory(
        @Header("Authorization") auth: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody
    ) : ResponseUploadStory


}