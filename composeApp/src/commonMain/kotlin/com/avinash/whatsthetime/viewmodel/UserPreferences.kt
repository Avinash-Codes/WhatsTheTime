package com.avinash.whatsthetime.viewmodel

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

//class UserPreferences(private val dataStore: DataStore<Preferences>) {
//    companion object {
//            val PROFILE_IMAGE_KEY = stringPreferencesKey("profile_image")
//        }
//
//    val profileImageUri: Flow<String?> = dataStore.data.map { preferences ->
//        preferences[PROFILE_IMAGE_KEY]
//    }
//
//    suspend fun saveProfileImageUri(uri: String) {
//        dataStore.edit { preferences ->
//            preferences[PROFILE_IMAGE_KEY] = uri
//        }
//    }
//}
