package com.dicoding.storyapp.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.Status
import com.dicoding.storyapp.data.local.UserPreference
import com.dicoding.storyapp.data.model.UserModel
import com.dicoding.storyapp.data.remote.response.LoginResult
import com.dicoding.storyapp.databinding.ActivityLoginBinding
import com.dicoding.storyapp.utils.isValidEmail

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val factory: ViewModelFactory = ViewModelFactory.getInstance(this@LoginActivity)
    private val viewModel: UserViewModel by viewModels {
        factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
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


    private fun setupAction() {
        binding.signUpButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.loginButton.setOnClickListener { btn ->
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            when {
                email.isEmpty() -> {
                    binding.emailEditTextLayout.error = getString(R.string.email_hint)
                }
                password.isEmpty() -> {
                    binding.passwordEditTextLayout.error = getString(R.string.password_hint)
                }
                else -> {
                    viewModel.loginUser(UserModel(email = email, password = password))
                        .observe(this) {
                            when (it) {
                                is Status.Loading -> {
                                    binding.progressBar.visibility = View.VISIBLE
                                    btn.isEnabled = false
                                    binding.signUpButton.isEnabled = false
                                }
                                is Status.Success -> {
                                    binding.progressBar.visibility = View.GONE
                                    val data = it.data
                                    Toast.makeText(
                                        this,
                                        getString(R.string.login_success_msg),
                                        Toast.LENGTH_LONG
                                    ).show()
                                    if (!data.error) getLogin(data.loginResult)
                                }
                                is Status.Error -> {
                                    binding.progressBar.visibility = View.GONE
                                    btn.isEnabled = true
                                    binding.signUpButton.isEnabled = true
                                    var message = ""
                                    when {
                                        it.error.contains("400") -> {
                                            message = getString(R.string.email_error_msg)
                                        }
                                        it.error.contains("401") -> {
                                            message = getString(R.string.login_error_msg)
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

    private fun getLogin(data: LoginResult) {
        val mUserPreference = UserPreference(this)
        mUserPreference.setUser(UserModel(name = data.name, token = data.token, isLogin = true))
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
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
}