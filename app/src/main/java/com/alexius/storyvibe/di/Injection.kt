package com.alexius.storyvibe.di

import android.content.Context
import com.alexius.storyvibe.data.Repository
import com.alexius.storyvibe.data.pref.LoginDatastore
import com.alexius.storyvibe.data.pref.dataStore
import com.alexius.storyvibe.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

object Injection {
    fun provideRepository(context: Context): Repository {
        val datastore = LoginDatastore.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService(datastore)
        return Repository.getInstance(apiService, datastore)
    }
}