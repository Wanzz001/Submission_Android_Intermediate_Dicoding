package org.d3if0080.mystoryapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.d3if0080.mystoryapp.R
import org.d3if0080.mystoryapp.api.DataRepository
import org.d3if0080.mystoryapp.api.services.ApiClient
import org.d3if0080.mystoryapp.databinding.ActivityRegisterBinding
import org.d3if0080.mystoryapp.utils.NetworkResult
import org.d3if0080.mystoryapp.utils.ViewModelFactory
import org.d3if0080.mystoryapp.viewmodel.AuthViewModel

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: AuthViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val dataRepository = DataRepository(ApiClient.getInstance())
        viewModel = ViewModelProvider(this, ViewModelFactory(dataRepository))[AuthViewModel::class.java]

        binding.btnRegister.setOnClickListener {
            val name = binding.edRegisterName.text.toString().trim()
            val email = binding.edRegisterEmail.text.toString().trim()
            val password = binding.edRegisterPassword.text.toString().trim()
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(name)) {
                Toast.makeText(this, getString(R.string.fill_in_all_fields), Toast.LENGTH_SHORT).show()
            } else {
                isLoading(true)
                lifecycleScope.launch {
                    viewModel.register(name, email, password).collect { result ->
                        when(result) {
                            is NetworkResult.Success -> {
                                isLoading(false)
                                startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                                Toast.makeText(this@RegisterActivity,
                                    getString(R.string.register_successfully), Toast.LENGTH_SHORT).show()
                                finish()
                            }
                            is NetworkResult.Loading -> {
                                isLoading(true)
                            }
                            is NetworkResult.Error -> {
                                Toast.makeText(this@RegisterActivity,
                                    getString(R.string.register_failed), Toast.LENGTH_SHORT).show()
                                isLoading(false)
                            }
                        }
                    }
                }
            }
        }

        binding.tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun isLoading(loading: Boolean) {
        when(loading) {
            true -> {
                binding.progressBar.visibility = View.VISIBLE
            }
            false -> {
                binding.progressBar.visibility = View.GONE
            }
        }
    }
}