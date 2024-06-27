package org.d3if0080.mystoryapp.viewmodel

import androidx.lifecycle.ViewModel
import org.d3if0080.mystoryapp.api.DataRepository

class AuthViewModel(private val dataRepository: DataRepository) : ViewModel() {

    suspend fun register(name: String, email: String, password: String) = dataRepository.register(name, email, password)

    suspend fun login(email: String, password: String) = dataRepository.login(email, password)

}