package com.alexius.storyvibe.view.signup

import androidx.lifecycle.ViewModel
import com.alexius.storyvibe.data.Repository

class SignUpViewModel(private val repository: Repository) : ViewModel() {
    fun registerUser(name: String, email: String, password: String) = repository.registerUser(name, email, password)
}