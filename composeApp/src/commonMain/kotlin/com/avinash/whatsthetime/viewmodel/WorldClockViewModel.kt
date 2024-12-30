package com.avinash.whatsthetime.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.avinash.whatsthetime.dataclass.ClockItem
import com.avinash.whatsthetime.dataclass.cites
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class WorldClockViewModel(
    private val dataStore: DataStore<Preferences>
) {
    private val viewModelScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val _isTwelveHourFormat = MutableStateFlow(false)
    val isTwelveHourFormat: StateFlow<Boolean> = _isTwelveHourFormat

    private val _clocks = mutableStateListOf<ClockItem>()
    val clocks: List<ClockItem> = _clocks

    private val _currentIndex = mutableStateOf(0)
    val currentIndex: State<Int> = _currentIndex

    private val _searchQuery = mutableStateOf("")
    val searchQuery: State<String> = _searchQuery

    private val _searchResults = MutableStateFlow<List<Pair<String, TimeZone>>>(emptyList())
    val searchResults: StateFlow<List<Pair<String, TimeZone>>> = _searchResults.asStateFlow()

    private val _username = MutableStateFlow("Sarah Jamel")
    val username: StateFlow<String> = _username

    private val _profileImageUri = MutableStateFlow<String?>(null)
    val profileImageUri: StateFlow<String?> = _profileImageUri.asStateFlow()

    companion object {
        private val SAVED_CLOCKS_KEY = stringPreferencesKey("saved_clocks")
        private val PROFILE_IMAGE_KEY = stringPreferencesKey("profile_image")
        val USERNAME_KEY = stringPreferencesKey("Avinash")
        val TWELVE_HOUR_FORMAT_KEY = booleanPreferencesKey("twelve_hour_format")

    }

    init {
        loadSavedClocks()
        loadProfileImageUri()
        viewModelScope.launch {
            username1.collect{ username ->
                _username.value = username ?: "Sarah Jamel"
            }
        }
        loadTwelveHourFormat()
    }

    private fun loadTwelveHourFormat() {
        viewModelScope.launch {
            dataStore.data.map { preferences ->
                preferences[TWELVE_HOUR_FORMAT_KEY] ?: false
            }.collect { isTwelveHour ->
                _isTwelveHourFormat.value = isTwelveHour
            }
        }
    }

    fun updateTwelveHourFormat(isTwelveHour: Boolean) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[TWELVE_HOUR_FORMAT_KEY] = isTwelveHour
            }
            _isTwelveHourFormat.value = isTwelveHour
        }
    }

    val username1: Flow<String?> = dataStore.data.map { preferences ->
        preferences[USERNAME_KEY]
    }

    suspend fun saveUsername(username: String) {
        dataStore.edit { preferences ->
            preferences[USERNAME_KEY] = username
        }
    }

    fun updateUsername(newUsername: String) {
        viewModelScope.launch {
            saveUsername(newUsername)
            _username.value = newUsername
        }
    }

    fun addClock(city: String, timeZoneId: String) {
        if (!_clocks.any { it.city == city }) {
            _clocks.add(ClockItem(city, timeZoneId))
            saveClocks()
        }
    }

    fun updateCurrentIndex(index: Int) {
        _currentIndex.value = index
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        updateSearchResults(query)
    }

    private fun updateSearchResults(query: String) {
        if (query.isEmpty()) {
            _searchResults.value = emptyList()
            return
        }

        val filteredResults = cites
            .filter { it.name.contains(query, ignoreCase = true) }
            .map { it.name to it.timeZoneId }
            .sortedWith(compareBy({!it.first.startsWith(query, ignoreCase = true)}, {it.first}))
            .take(5) // Limit results to top 5 matches

        _searchResults.value = filteredResults
    }

    fun removeClock(clockItem: ClockItem) {
        _clocks.remove(clockItem)
        saveClocks()
    }

    fun getFilteredCities(): List<Pair<String, TimeZone>> {
        return searchResults.value
    }

    private fun saveClocks() {
        viewModelScope.launch {
            try {
                val clocksJson = Json.encodeToString(_clocks.toList())
                dataStore.edit { preferences ->
                    preferences[SAVED_CLOCKS_KEY] = clocksJson
                }
            } catch (e: Exception) {
                println("Error saving clocks: ${e.message}")
            }
        }
    }

    private fun loadSavedClocks() {
        viewModelScope.launch {
            try {
                val clocksJson = dataStore.data.first()[SAVED_CLOCKS_KEY] ?: return@launch
                val savedClocks = Json.decodeFromString<List<ClockItem>>(clocksJson)
                _clocks.clear()
                _clocks.addAll(savedClocks)
            } catch (e: Exception) {
                println("Error loading clocks: ${e.message}")
            }
        }
    }

//    val profileImageUri: Flow<String?> = dataStore.data.map { preferences ->
//        preferences[PROFILE_IMAGE_KEY]
//    }

    fun saveProfileImageUri(uri: String) {
        viewModelScope.launch {
            try {
                dataStore.edit { preferences ->
                    preferences[PROFILE_IMAGE_KEY] = uri
                }
                _profileImageUri.value = uri  // Update the StateFlow immediately
            } catch (e: Exception) {
                println("Error saving profile image uri: ${e.message}")
            }
        }
    }

    private fun loadProfileImageUri() {
        viewModelScope.launch {
            try {
                dataStore.data.collect { preferences ->  // Use collect instead of first
                    _profileImageUri.value = preferences[PROFILE_IMAGE_KEY]
                }
            } catch (e: Exception) {
                println("Error loading profile image uri: ${e.message}")
            }
        }
    }
}
