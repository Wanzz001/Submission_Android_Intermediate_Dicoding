package org.d3if0080.mystoryapp.api

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.d3if0080.mystoryapp.api.response.home.ResponseHome
import org.d3if0080.mystoryapp.api.response.login.ResponseLogin
import org.d3if0080.mystoryapp.api.response.register.ResponseRegister
import org.d3if0080.mystoryapp.api.response.upload.ResponseUploadStory
import org.d3if0080.mystoryapp.api.services.ApiService
import org.d3if0080.mystoryapp.database.Story
import org.d3if0080.mystoryapp.database.StoryDatabase
import org.d3if0080.mystoryapp.database.StoryRemoteMediator
import org.d3if0080.mystoryapp.utils.NetworkResult
import retrofit2.HttpException
import java.io.File

class DataRepository(private val apiService: ApiService, private val database: StoryDatabase? = null) {

    suspend fun getStoriesLocation(auth: String) : Flow<NetworkResult<ResponseHome>> =
        flow {
            try {
                val generateToken = generateAuthorization(auth)
                val response = apiService.getStories(auth = generateToken, location = 1)
                emit(NetworkResult.Success(response))
            } catch (e : Exception) {
                val ex = (e as? HttpException)?.response()?.errorBody()?.string()
                emit(NetworkResult.Error(ex.toString()))
            }
        }.flowOn(Dispatchers.IO)

    @OptIn(ExperimentalPagingApi::class)
    fun getStories(auth: String): Flow<PagingData<Story>> {
        val pagingSourceFactory = { database?.storyDao()?.getStories()!! }

        return Pager(
            config = PagingConfig(pageSize = 10),
            remoteMediator = StoryRemoteMediator(database!!, apiService, auth),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }


    suspend fun uploadStory(auth: String, description: String, file: File, lat: String?, lon: String?) : Flow<NetworkResult<ResponseUploadStory>> =
        flow {
            try {
                val generateToken = generateAuthorization(auth)
                val desc = description.toRequestBody("text/plain".toMediaType())
                val requestImageFile = file.asRequestBody("image/jpg".toMediaTypeOrNull())
                val latitude = lat?.toRequestBody("text/plain".toMediaType())
                val longitude = lon?.toRequestBody("text/plain".toMediaType())
                val imageMultipart = MultipartBody.Part.createFormData(
                    "photo",
                    file.name,
                    requestImageFile
                )
                val response = apiService.uploadStory(generateToken,imageMultipart,desc, latitude, longitude)
                emit(NetworkResult.Success(response))
            } catch (e : Exception) {
                val ex = (e as? HttpException)?.response()?.errorBody()?.string()
                emit(NetworkResult.Error(ex.toString()))
            }
        }.flowOn(Dispatchers.IO)

    suspend fun register(
        name: String,
        email: String,
        password: String
    ): Flow<NetworkResult<ResponseRegister>> = flow {
        try {
            val response = apiService.register(name, email, password)
            emit(NetworkResult.Success(response))
        } catch(e : Exception) {
            e.printStackTrace()
            emit(NetworkResult.Error(e.toString()))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun login(
        email: String,
        password: String
    ): Flow<NetworkResult<ResponseLogin>> = flow {
        try {
            val response = apiService.login(email, password)
            emit(NetworkResult.Success(response))
        } catch(e : Exception) {
            e.printStackTrace()
            emit(NetworkResult.Error(e.toString()))
        }
    }.flowOn(Dispatchers.IO)

    private fun generateAuthorization(token: String) : String {
        return "Bearer $token"
    }

}