package com.alexius.storyvibe.data.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "loginData")

class LoginDatastore private constructor(private val dataStore: DataStore<Preferences>) {

    private val LOGIN_TOKEN = stringPreferencesKey("login_token")
    private val IS_LOGIN = booleanPreferencesKey("is_login")

    fun getLoginToken(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[LOGIN_TOKEN] ?: ""
        }
    }

    fun getIsLogin(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[IS_LOGIN] ?: false
        }
    }

    suspend fun saveLoginToken(token: String) {
        dataStore.edit { preferences ->
            preferences[LOGIN_TOKEN] = token
        }
    }

    suspend fun deleteLoginToken() {
        dataStore.edit { preferences ->
            preferences.remove(LOGIN_TOKEN)
        }
    }

    suspend fun saveIsLogin(isLogin: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_LOGIN] = isLogin
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: LoginDatastore? = null

        fun getInstance(dataStore: DataStore<Preferences>): LoginDatastore {
            return INSTANCE ?: synchronized(this) {
                val instance = LoginDatastore(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}
