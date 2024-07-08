package org.d3if0080.mystoryapp.ui

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import org.d3if0080.mystoryapp.R
import org.d3if0080.mystoryapp.adapter.StoryListAdapter
import org.d3if0080.mystoryapp.api.DataRepository
import org.d3if0080.mystoryapp.api.services.ApiClient
import org.d3if0080.mystoryapp.databinding.ActivityMainBinding
import org.d3if0080.mystoryapp.utils.NetworkResult
import org.d3if0080.mystoryapp.utils.UserPrefs
import org.d3if0080.mystoryapp.utils.ViewModelFactory
import org.d3if0080.mystoryapp.viewmodel.ListStoryViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: ListStoryViewModel
    private lateinit var userPrefs: UserPrefs
    private lateinit var adapter: StoryListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        userPrefs = UserPrefs(this)
        val dataRepository = DataRepository(ApiClient.getInstance())
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(dataRepository)
        )[ListStoryViewModel::class.java]
        lifecycleScope.launch {
            fetchData(userPrefs.token)
        }

        adapter = StoryListAdapter(this)
        adapter.setOnItemClickCallback { selectedStory, options ->
            val intent = Intent(this, DetailActivity::class.java).apply {
                putExtra(DetailActivity.EXTRA_ITEM, selectedStory)
            }
            startActivity(intent, options.toBundle())
        }

        with(binding) {
            rvStory.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(this@MainActivity)
                adapter = this@MainActivity.adapter
            }

            btnRefresh.setOnClickListener {
                lifecycleScope.launch {
                    fetchData(userPrefs.token)
                }
            }
            fabAddStory.setOnClickListener {
                startActivity(Intent(this@MainActivity, AddStoryActivity::class.java))
            }
        }
    }

    private suspend fun fetchData(token: String) {
        isLoading(true)
        viewModel.fetchListStory(token)
        viewModel.responseListStory.observe(this@MainActivity) {
            with(binding) {
                when (it) {
                    is NetworkResult.Success -> {
                        if (it.data?.listStory != null) {
                            rvStory.visibility = View.VISIBLE
                            tvError.visibility = View.GONE
                            btnRefresh.visibility = View.GONE
                            adapter.setData(it.data.listStory)
                        } else {
                            rvStory.visibility = View.GONE
                            tvError.text = getString(R.string.data_not_found)
                            btnRefresh.visibility = View.VISIBLE
                            tvError.visibility = View.VISIBLE
                        }
                        isLoading(false)
                    }

                    is NetworkResult.Loading -> {
                        isLoading(true)
                    }

                    is NetworkResult.Error -> {
                        isLoading(false)
                        rvStory.visibility = View.GONE
                        tvError.text = getString(R.string.failed_to_get_data)
                        tvError.visibility = View.VISIBLE
                        btnRefresh.visibility = View.VISIBLE
                    }
                }
            }
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_item, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }

            R.id.logout -> {
                val dialog = AlertDialog.Builder(this)
                dialog.setTitle(resources.getString(R.string.log_out))
                dialog.setMessage(getString(R.string.are_you_sure))
                dialog.setPositiveButton(getString(R.string.yes)) { _, _ ->
                    userPrefs.clear()
                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                    Toast.makeText(
                        this@MainActivity,
                        getString(R.string.you_logged_out),
                        Toast.LENGTH_SHORT
                    ).show()
                    this@MainActivity.finish()
                }
                dialog.setNegativeButton(getString(R.string.no)) { _, _ ->
                }
                dialog.show()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
