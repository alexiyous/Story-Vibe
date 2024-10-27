package com.alexius.storyvibe.view.login

import androidx.lifecycle.ViewModel
import com.alexius.storyvibe.data.Repository

class LoginViewModel(private val repository: Repository) : ViewModel() {
    fun login(email: String, password: String) = repository.login(email, password)
}