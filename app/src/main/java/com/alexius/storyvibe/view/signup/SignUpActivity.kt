package com.alexius.storyvibe.view.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.alexius.storyvibe.R
import com.alexius.storyvibe.databinding.ActivitySignUpBinding
import com.alexius.storyvibe.view.ViewModelFactory
import com.alexius.storyvibe.data.Result

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding

    private val viewModel by viewModels<SignUpViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT))
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupAction()
        setupAnimation()
    }

    private fun setupAction() {
        binding.signupButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditTextLayout.getText()
            viewModel.registerUser(name, email, password).observe(this) { response ->
                if (response != null) {
                    when (response) {
                        is Result.Loading -> {
                            binding.progressIndicator.visibility = View.VISIBLE
                            binding.signupButton.isEnabled = false
                        }
                        is Result.Success -> {
                            binding.progressIndicator.visibility = View.GONE
                            binding.signupButton.isEnabled = true

                            if (!response.data.error) {
                                AlertDialog.Builder(this)
                                    .setTitle("Success")
                                    .setMessage("Registration success")
                                    .setPositiveButton("OK") { dialog, _ ->
                                        dialog.dismiss()
                                        finish()
                                    }
                                    .show()
                            }
                        }
                        is Result.Error -> {
                            binding.progressIndicator.visibility = View.GONE
                            binding.signupButton.isEnabled = true
                            AlertDialog.Builder(this)
                                .setTitle("Error")
                                .setMessage(response.error)
                                .setPositiveButton("OK") { dialog, _ ->
                                    dialog.dismiss()
                                }
                                .show()
                        }
                    }
                }

            }
        }
    }

    private fun setupAnimation() {
        val imageView = ObjectAnimator.ofFloat(binding.imageView, View.ALPHA, 1f).setDuration(200)
        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(200)
        val nameText = ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(200)
        val nameEdit = ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(200)
        val emailText = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(200)
        val emailEdit = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(200)
        val passwordText = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(200)
        val passwordEdit = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(200)
        val signup = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(200)

        AnimatorSet().apply {
            playSequentially(imageView, title, nameText, nameEdit, emailText, emailEdit, passwordText, passwordEdit, signup)
            startDelay = 100
            start()
        }
    }
}