package com.alexius.storyvibe.view.welcome

import androidx.lifecycle.ViewModel
import com.alexius.storyvibe.data.Repository

class WelcomeViewModel(private val repository: Repository) : ViewModel() {

    fun checkIsLogin() = repository.checkIsLogin()
}