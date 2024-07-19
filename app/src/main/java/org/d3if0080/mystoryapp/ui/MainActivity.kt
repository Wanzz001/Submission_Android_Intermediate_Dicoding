package org.d3if0080.mystoryapp.ui

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import org.d3if0080.mystoryapp.R
import org.d3if0080.mystoryapp.adapter.LoadingStateAdapter
import org.d3if0080.mystoryapp.adapter.StoryListAdapter
import org.d3if0080.mystoryapp.api.DataRepository
import org.d3if0080.mystoryapp.api.services.ApiClient
import org.d3if0080.mystoryapp.database.StoryDatabase
import org.d3if0080.mystoryapp.databinding.ActivityMainBinding
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
        val database = StoryDatabase.getDatabase(this)
        val dataRepository = DataRepository(ApiClient.getInstance(), database)
        viewModel = ViewModelProvider(this, ViewModelFactory(dataRepository))[ListStoryViewModel::class.java]
        adapter = StoryListAdapter(this)
        adapter.setOnItemClickCallback { selectedStory, options ->
            val intent = Intent(this, DetailActivity::class.java).apply {
                putExtra(DetailActivity.EXTRA_ITEM, selectedStory)
            }
            startActivity(intent, options.toBundle())
        }
        fetchData(userPrefs.token)

        with(binding) {

            btnRefresh.setOnClickListener {
                fetchData(userPrefs.token)
            }
            fabAddStory.setOnClickListener {
                startActivity(Intent(this@MainActivity, AddStoryActivity::class.java))
            }
            btnMap.setOnClickListener {
                startActivity(Intent(this@MainActivity, MapsActivity::class.java))
            }
        }
    }

    private fun fetchData(token: String) {
        binding.apply {
            rvStory.setHasFixedSize(true)
            rvStory.layoutManager = LinearLayoutManager(this@MainActivity)
            rvStory.adapter = adapter.withLoadStateHeaderAndFooter(
                header = LoadingStateAdapter { adapter.retry() },
                footer = LoadingStateAdapter { adapter.retry() }
            )
        }

        viewModel.getStories(token).observe(this) {
            adapter.submitData(lifecycle, it)
        }

        adapter.addLoadStateListener {
            binding.apply {
                progressBar.isVisible = it.source.refresh is LoadState.Loading
                rvStory.isVisible = it.source.refresh is LoadState.NotLoading
                tvError.isVisible = it.source.refresh is LoadState.Error
                btnRefresh.isVisible = it.source.refresh is LoadState.Error

                if (it.source.refresh is LoadState.NotLoading && it.append.endOfPaginationReached && adapter.itemCount < 1) {
                    tvError.text = getString(R.string.data_not_found)
                    rvStory.isVisible = false
                } else {
                    tvError.isVisible = false
                    rvStory.isVisible = true
                }
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
