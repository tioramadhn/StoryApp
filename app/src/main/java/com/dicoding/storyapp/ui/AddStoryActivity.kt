package com.dicoding.storyapp.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.Status
import com.dicoding.storyapp.data.local.UserPreference
import com.dicoding.storyapp.data.model.StoryModel
import com.dicoding.storyapp.databinding.ActivityAddStoryBinding
import com.dicoding.storyapp.utils.createTempFile
import com.dicoding.storyapp.utils.rotateBitmap
import com.dicoding.storyapp.utils.uriToFile
import java.io.File

class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private var getFile: File? = null
    private lateinit var currentPhotoPath: String
    private val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
    private val viewModel: UserViewModel by viewModels {
        factory
    }
    private var isFromCamera: Boolean = false

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = getString(R.string.add_story)
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
        binding.btnCamera.setOnClickListener { startTakePhoto() }
        binding.btnGalery.setOnClickListener { startGallery() }
        binding.uploadButton.setOnClickListener { btn ->
            val mUserPreference = UserPreference(this)
            val userModel = mUserPreference.getUser()
            val description = binding.edtDescription.text.toString()
            when {
                description.isEmpty() -> {
                    binding.edtDescription.error = getString(R.string.description_error)
                }
                getFile == null -> {
                    Toast.makeText(
                        this,
                        getString(R.string.upload_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else -> {
                    val story = getFile?.let { StoryModel(it, description) }
                    val token = userModel.token
                    if (story != null && token != null) {
                        viewModel.addNewStory(story, token, isFromCamera).observe(this) { result ->
                            when (result) {
                                is Status.Loading -> {
                                    Log.d("AddStory", "CHECKPOINT LOADING")
                                    binding.progressBar.visibility = View.VISIBLE
                                    btn.isEnabled = false
                                    binding.btnGalery.isEnabled = false
                                    binding.btnCamera.isEnabled = false
                                }
                                is Status.Success -> {
                                    Log.d("AddStory", "CHECKPOINT SUKSES")
                                    binding.progressBar.visibility = View.GONE
                                    if (!result.data.error) {
                                        Toast.makeText(
                                            this,
                                            getString(R.string.upload_succes),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        startActivity(Intent(this, MainActivity::class.java))
                                        finish()
                                    }
                                }
                                is Status.Error -> {
                                    Log.d("AddStory", "CHECKPOINT ERROR")
                                    binding.progressBar.visibility = View.GONE
                                    binding.btnGalery.isEnabled = true
                                    binding.btnCamera.isEnabled = true
                                    btn.isEnabled = true
                                    Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                                }

                            }

                        }
                    }
                }


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
                Toast.makeText(
                    this,
                    getString(R.string.permission),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)
        createTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@AddStoryActivity,
                "com.dicoding.storyapp",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, getString(R.string.choose_photo))
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this)
            getFile = myFile
            isFromCamera = false
            binding.imgPreview.setImageURI(selectedImg)
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            getFile = myFile
            val result = rotateBitmap(
                BitmapFactory.decodeFile(myFile.path),
                true
            )
            isFromCamera = true
            binding.imgPreview.setImageBitmap(result)
        }
    }
}