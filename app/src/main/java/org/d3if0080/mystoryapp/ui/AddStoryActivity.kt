package org.d3if0080.mystoryapp.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.d3if0080.mystoryapp.R
import org.d3if0080.mystoryapp.api.DataRepository
import org.d3if0080.mystoryapp.api.services.ApiClient
import org.d3if0080.mystoryapp.databinding.ActivityAddStoryBinding
import org.d3if0080.mystoryapp.utils.NetworkResult
import org.d3if0080.mystoryapp.utils.UserPrefs
import org.d3if0080.mystoryapp.utils.ViewModelFactory
import org.d3if0080.mystoryapp.utils.createCustomTempFile
import org.d3if0080.mystoryapp.utils.reduceFileImage
import org.d3if0080.mystoryapp.utils.uriToFile
import org.d3if0080.mystoryapp.viewmodel.UploadStoryViewModel
import java.io.File

class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var viewModel: UploadStoryViewModel
    private lateinit var userPrefs: UserPrefs
    private lateinit var currentPhotoPath: String
    private var imgFile: File? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        userPrefs = UserPrefs(this)
        val dataRepository = DataRepository(ApiClient.getInstance())
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(dataRepository)
        )[UploadStoryViewModel::class.java]

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        binding.btnCam.setOnClickListener {
            startTakePhoto()
        }
        binding.btnGallery.setOnClickListener {
            startGallery()
        }
        binding.btnUpload.setOnClickListener {
            if(imgFile != null || !TextUtils.isEmpty(binding.edtDesc.text.toString())) {
                uploadStory(userPrefs.token)
            } else {
                Toast.makeText(this, getString(R.string.data_not_complete), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(this, getString(R.string.not_allowed), Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)
        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@AddStoryActivity,
                "org.d3if0080.mystoryapp.ui",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            imgFile = myFile
            val result = BitmapFactory.decodeFile(myFile.path)
            binding.ivPrev.setImageBitmap(result)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@AddStoryActivity)
            imgFile = myFile

            binding.ivPrev.setImageURI(selectedImg)
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, getString(R.string.choose_a_picture))
        launcherIntentGallery.launch(chooser)
    }

    private fun uploadStory(auth: String) {
        isLoading(true)
        val file = reduceFileImage(imgFile as File)
        val description = binding.edtDesc.text.toString().trim()
        lifecycleScope.launch {
            viewModel.uploadStory(auth, description, file).collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        isLoading(false)
                        Toast.makeText(this@AddStoryActivity,
                            getString(R.string.add_story_successfully), Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@AddStoryActivity, MainActivity::class.java))
                        finish()
                    }
                    is NetworkResult.Loading -> {
                        isLoading(true)
                    }
                    is NetworkResult.Error -> {
                        isLoading(false)
                        Toast.makeText(this@AddStoryActivity,
                            getString(R.string.add_story_failed), Toast.LENGTH_SHORT).show()
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

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}