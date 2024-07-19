package org.d3if0080.mystoryapp.viewmodel

import androidx.lifecycle.ViewModel
import org.d3if0080.mystoryapp.api.DataRepository
import java.io.File

class UploadStoryViewModel(private val dataRepository: DataRepository) : ViewModel() {

    suspend fun uploadStory(auth: String, description: String, file: File, lat: String?, lon: String?) =
        dataRepository.uploadStory(auth, description, file, lat, lon)
}