package com.alexius.storyvibe.view.homepage

import androidx.lifecycle.ViewModel
import com.alexius.storyvibe.data.Repository

class HomeViewModel(private val repository: Repository) : ViewModel() {

    fun getAllStories() = repository.getAllStories()
    fun logout() = repository.logout()
    fun getAllStoriesByPager() = repository.getStoryByPager()
}