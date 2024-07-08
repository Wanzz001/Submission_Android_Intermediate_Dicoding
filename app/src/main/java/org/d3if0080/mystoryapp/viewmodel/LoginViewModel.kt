package org.d3if0080.mystoryapp.viewmodel

import androidx.lifecycle.ViewModel
import org.d3if0080.mystoryapp.api.DataRepository

class LoginViewModel(private val dataRepository: DataRepository) : ViewModel() {
    suspend fun login(email: String, password: String) = dataRepository.login(email, password)

}