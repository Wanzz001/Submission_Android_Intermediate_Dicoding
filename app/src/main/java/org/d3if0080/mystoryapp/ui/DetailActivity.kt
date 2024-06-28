package org.d3if0080.mystoryapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import org.d3if0080.mystoryapp.api.response.home.ListStory
import org.d3if0080.mystoryapp.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val intent = intent.getParcelableExtra<ListStory>(EXTRA_ITEM)
        binding.tvTitle.text = intent?.name
        binding.tvDesc.text = intent?.description
        Glide.with(this).load(intent?.photoUrl).into(binding.ivImgStory)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    companion object {
        const val EXTRA_ITEM = "extra_item"
    }
}