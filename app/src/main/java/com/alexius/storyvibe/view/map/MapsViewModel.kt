package com.alexius.storyvibe.view.map

import androidx.lifecycle.ViewModel
import com.alexius.storyvibe.data.Repository

class MapsViewModel(private val repository: Repository) : ViewModel() {

    fun getAllStoriesWithLoaction() = repository.getAllStoriesWithLocation()
}