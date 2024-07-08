package org.d3if0080.mystoryapp.ui

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.d3if0080.mystoryapp.R
import org.d3if0080.mystoryapp.api.DataRepository
import org.d3if0080.mystoryapp.api.services.ApiClient
import org.d3if0080.mystoryapp.databinding.ActivityLoginBinding
import org.d3if0080.mystoryapp.utils.NetworkResult
import org.d3if0080.mystoryapp.utils.UserPrefs
import org.d3if0080.mystoryapp.utils.ViewModelFactory
import org.d3if0080.mystoryapp.viewmodel.LoginViewModel

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel
    private lateinit var userPrefs: UserPrefs
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userPrefs = UserPrefs(this)
        val dataRepository = DataRepository(ApiClient.getInstance())
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(dataRepository)
        )[LoginViewModel::class.java]

        binding.btnLogin.setOnClickListener {
            val email = binding.edLoginEmail.text.toString().trim()
            val password = binding.edLoginPassword.text.toString().trim()
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, getString(R.string.fill_in_all_fields), Toast.LENGTH_SHORT)
                    .show()
            } else {
                isLoading(true)
                lifecycleScope.launch {
                    viewModel.login(email, password).collect { result ->
                        when (result) {
                            is NetworkResult.Success -> {
                                userPrefs.isLoggedIn = !result.data?.error!!
                                userPrefs.token = result.data.result.token
                                isLoading(false)
                                startActivity(
                                    Intent(
                                        this@LoginActivity,
                                        MainActivity::class.java
                                    )
                                )
                                Toast.makeText(
                                    this@LoginActivity,
                                    getString(R.string.login_successfully), Toast.LENGTH_SHORT
                                ).show()
                                finish()
                            }

                            is NetworkResult.Loading -> {
                                isLoading(true)
                            }

                            is NetworkResult.Error -> {
                                Toast.makeText(
                                    this@LoginActivity,
                                    getString(R.string.login_failed), Toast.LENGTH_SHORT
                                ).show()
                                isLoading(false)
                            }
                        }
                    }
                }
            }
        }
        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }

    private fun isLoading(loading: Boolean) {
        when (loading) {
            true -> {
                binding.progressBar.visibility = View.VISIBLE
            }

            false -> {
                binding.progressBar.visibility = View.GONE
            }
        }
    }
}