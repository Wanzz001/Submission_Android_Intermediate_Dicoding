package org.d3if0080.mystoryapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.collectLatest
import org.d3if0080.mystoryapp.api.DataRepository
import org.d3if0080.mystoryapp.api.response.home.ResponseHome
import org.d3if0080.mystoryapp.utils.NetworkResult

class ListStoryViewModel(private val dataRepository: DataRepository): ViewModel() {

    private val listStory = MutableLiveData<NetworkResult<ResponseHome>>()
    val responseListStory: LiveData<NetworkResult<ResponseHome>> = listStory

    suspend fun fetchListStory(auth: String) {
            dataRepository.getStories(auth).collectLatest {
                listStory.value = it

        }
    }
}