package org.d3if0080.mystoryapp.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.d3if0080.mystoryapp.R
import org.d3if0080.mystoryapp.utils.UserPrefs

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        supportActionBar?.hide()
        val userPrefs = UserPrefs(this)
        lifecycleScope.launch {
            delay(2000)
            val intent = if(userPrefs.isLoggedIn) {
                Intent(this@SplashScreen, MainActivity::class.java)
            } else {
                Intent(this@SplashScreen, LoginActivity::class.java)
            }
            startActivity(intent)
            finish()
        }
    }
}