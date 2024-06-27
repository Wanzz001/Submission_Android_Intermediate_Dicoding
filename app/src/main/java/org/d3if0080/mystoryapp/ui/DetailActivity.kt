package org.d3if0080.mystoryapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.d3if0080.mystoryapp.R

class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
    }

    companion object {
        const val EXTRA_ITEM = "extra_item"
    }
}