package com.alexius.storyvibe.data

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.alexius.storyvibe.data.pref.LoginDatastore
import com.alexius.storyvibe.data.remote.paging.StoryPagingSource
import com.alexius.storyvibe.data.remote.response.AddStoryResponse
import com.alexius.storyvibe.data.remote.response.ErrorResponse
import com.alexius.storyvibe.data.remote.response.ListStoryItem
import com.alexius.storyvibe.data.remote.response.LoginResponse
import com.alexius.storyvibe.data.remote.response.RegisterResponse
import com.alexius.storyvibe.data.remote.response.StoryResponse
import com.alexius.storyvibe.data.remote.retrofit.ApiConfig
import com.alexius.storyvibe.data.remote.retrofit.ApiService
import com.alexius.storyvibe.utils.reduceFileImage
import com.alexius.storyvibe.utils.uriToFile
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException

class Repository private constructor(
    private val apiService: ApiService,
    private val datastore: LoginDatastore
) {

    fun registerUser(name: String, email: String, password: String): LiveData<Result<RegisterResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.register(name, email, password)
            emit(Result.Success(response))
        } catch (e: HttpException) {
            val errorResponse = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(errorResponse, ErrorResponse::class.java)
            val errorMessage = errorBody.message
            Log.d("Repository", "RegisterUser: $errorMessage")
            emit(Result.Error(errorMessage?:"Error"))
        }
    }

    fun login(email: String, password: String): LiveData<Result<LoginResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.login(email, password)
            val token = response.loginResult?.token
            datastore.saveLoginToken(token?: "")
            datastore.saveIsLogin(true)
            val tokenFromDatastore = datastore.getLoginToken().first()
            emit(Result.Success(response))
        } catch (e: HttpException) {
            val errorResponse = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(errorResponse, ErrorResponse::class.java)
            val errorMessage = errorBody.message
            Log.d("Repository", "Login: ${e.response()}")
            emit(Result.Error(errorMessage?:"Error"))
        }
    }

    fun checkIsLogin(): LiveData<Boolean> = liveData {
        val isLogin = datastore.getIsLogin().first()
        emit(isLogin)
    }

    fun logout(): LiveData<Boolean> = liveData {
        datastore.saveIsLogin(false)
        datastore.deleteLoginToken()
        emit(true)
    }

    fun getAllStories(): LiveData<Result<StoryResponse>> = liveData {
        emit(Result.Loading)
        try {
            val token = datastore.getLoginToken().first()
            Log.d("Repository", "GetAllStories: $token")
            val response = apiService.getStories()
            emit(Result.Success(response))
        } catch (e: HttpException) {
            val errorResponse = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(errorResponse, ErrorResponse::class.java)
            val errorMessage = errorBody.message
            Log.d("Repository", "GetAllStories: $errorMessage")
            emit(Result.Error(errorMessage?:"Error"))
        }
    }

    fun getStoryByPager(): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(apiService)
            }
        ).liveData
    }

    fun getAllStoriesWithLocation(): LiveData<Result<StoryResponse>> = liveData {
        emit(Result.Loading)
        try {
            val token = datastore.getLoginToken().first()
            Log.d("Repository", "GetAllStoriesWithLocation: $token")
            val response = apiService.getStoriesWithLocation()
            emit(Result.Success(response))
        } catch (e: HttpException) {
            val errorResponse = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(errorResponse, ErrorResponse::class.java)
            val errorMessage = errorBody.message
            Log.d("Repository", "GetAllStoriesWithLocation: $errorMessage")
            emit(Result.Error(errorMessage?:"Error"))
        }
    }

    fun uploadStory(imageUri: Uri?, context: Context, descriptionText: String): LiveData<Result<AddStoryResponse>> = liveData {
        emit(Result.Loading)
        try {
            if (imageUri != null) {
                val imageFile = uriToFile(imageUri, context).reduceFileImage()
                Log.d("Image File", "showImage: ${imageFile.path}")
                val requestBody = descriptionText.toRequestBody("text/plain".toMediaType())
                val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
                val multipartBody = MultipartBody.Part.createFormData(
                    "photo",
                    imageFile.name,
                    requestImageFile
                )
                val successResponse = apiService.uploadStory(multipartBody, requestBody)
                emit(Result.Success(successResponse))
            }
        } catch (e: HttpException) {
            val errorResponse = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(errorResponse, ErrorResponse::class.java)
            val errorMessage = errorBody.message
            Log.d("Repository", "UploadStory: $errorMessage")
            emit(Result.Error(errorMessage?:"Error"))
        }
    }

    companion object {
        @Volatile
        private var instance: Repository? = null
        fun getInstance(
            apiService: ApiService,
            datastore: LoginDatastore
        ): Repository =
            instance ?: synchronized(this) {
                instance ?: Repository(apiService, datastore)
            }.also { instance = it }
    }
}