package com.avinash.whatsthetime.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.avinash.whatsthetime.dataclass.ClockItem
import com.avinash.whatsthetime.dataclass.cites
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class WorldClockViewModel(
    private val dataStore: DataStore<Preferences>
) {
    private val viewModelScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val _clocks = mutableStateListOf<ClockItem>()
    val clocks: List<ClockItem> = _clocks

    private val _currentIndex = mutableStateOf(0)
    val currentIndex: State<Int> = _currentIndex

    private val _searchQuery = mutableStateOf("")
    val searchQuery: State<String> = _searchQuery

    private val _searchResults = MutableStateFlow<List<Pair<String, TimeZone>>>(emptyList())
    val searchResults: StateFlow<List<Pair<String, TimeZone>>> = _searchResults.asStateFlow()

    companion object {
        private val SAVED_CLOCKS_KEY = stringPreferencesKey("saved_clocks")
    }

    init {
        loadSavedClocks()
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
}