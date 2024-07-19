package org.d3if0080.mystoryapp.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import org.d3if0080.mystoryapp.api.DataRepository
import org.d3if0080.mystoryapp.api.response.login.ResponseLogin
import org.d3if0080.mystoryapp.utils.NetworkResult

class LoginViewModel(private val dataRepository: DataRepository) : ViewModel() {
    suspend fun login(email: String, password: String): Flow<NetworkResult<ResponseLogin>> {
        return dataRepository.login(email, password)
    }
}
