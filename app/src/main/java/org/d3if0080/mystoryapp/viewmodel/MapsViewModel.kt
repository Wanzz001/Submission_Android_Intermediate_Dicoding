package org.d3if0080.mystoryapp.viewmodel

import androidx.lifecycle.ViewModel
import org.d3if0080.mystoryapp.api.DataRepository

class MapsViewModel(private val dataRepository: DataRepository) : ViewModel() {
    suspend fun getStoriesLocation(auth: String) = dataRepository.getStoriesLocation(auth)
}