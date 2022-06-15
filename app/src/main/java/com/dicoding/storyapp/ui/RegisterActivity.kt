package com.dicoding.storyapp.ui

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.Status
import com.dicoding.storyapp.data.model.UserModel
import com.dicoding.storyapp.databinding.ActivityRegisterBinding
import com.dicoding.storyapp.utils.isValidEmail

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
    private val viewModel: UserViewModel by viewModels {
        factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        playAnimation()
    }
    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.signupButton.setOnClickListener { btn ->
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            when {
                name.isEmpty() -> {
                    binding.nameEditTextLayout.error = getString(R.string.name_hint)
                }
                email.isEmpty() -> {
                    binding.emailEditTextLayout.error = getString(R.string.email_hint)
                }
                password.isEmpty() -> {
                    binding.passwordEditTextLayout.error = getString(R.string.password_hint)
                }
                else -> {
                    viewModel.registUser(UserModel(name = name, email = email, password = password))
                        .observe(this) {
                            when (it) {
                                is Status.Loading -> {
                                    binding.progressBar.visibility = View.VISIBLE
                                    btn.isEnabled = false
                                }
                                is Status.Success -> {
                                    binding.progressBar.visibility = View.GONE
                                    val data = it.data
                                    Toast.makeText(
                                        this,
                                        data.message,
                                        Toast.LENGTH_LONG
                                    ).show()
                                    if (!data.error) getLogin()
                                }
                                is Status.Error -> {
                                    binding.progressBar.visibility = View.GONE
                                    btn.isEnabled = true
                                    var message = ""
                                    when {
                                        it.error.contains("400") -> {
                                            message = getString(R.string.email_error_msg2)
                                        }
                                        else -> message = getString(R.string.server_error_msg)
                                    }
                                    Toast.makeText(
                                        this,
                                        message,
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }

                }
            }
        }
    }

    private fun getLogin() {
        AlertDialog.Builder(this).apply {
            setTitle("Yeah!")
            setMessage(getString(R.string.succes_regist_msg))
            setPositiveButton(getString(R.string.next)) { _, _ ->
                val intent = Intent(context, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
            create()
            show()
        }
    }

}