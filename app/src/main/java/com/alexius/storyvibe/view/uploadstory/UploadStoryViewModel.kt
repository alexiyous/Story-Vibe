package com.alexius.storyvibe.view.uploadstory

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.alexius.storyvibe.data.Repository

class UploadStoryViewModel(private val repository: Repository) : ViewModel() {
    fun uploadStory(imageUri: Uri, context: Context, description: String) = repository.uploadStory(imageUri, context, description)

    fun getAllStories() = repository.getAllStories()
}