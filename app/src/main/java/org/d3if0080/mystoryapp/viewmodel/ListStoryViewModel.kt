package org.d3if0080.mystoryapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import org.d3if0080.mystoryapp.api.DataRepository
import org.d3if0080.mystoryapp.database.Story

class ListStoryViewModel(private val storyRepository: DataRepository): ViewModel() {

    fun getStories(auth: String): LiveData<PagingData<Story>> =
        storyRepository.getStories(auth).cachedIn(viewModelScope).asLiveData()
}