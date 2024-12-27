package com.avinash.whatsthetime.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.intl.Locale
import androidx.lifecycle.ViewModel
import com.avinash.whatsthetime.dataclass.ClockItem
import com.avinash.whatsthetime.dataclass.cites
import kotlinx.datetime.TimeZone

class WorldClockViewModel: ViewModel() {
    private val _clocks = mutableStateListOf<ClockItem>()
    val clocks: List<ClockItem> = _clocks

    private val _currentIndex = mutableStateOf(0)
    val currentIndex: State<Int> = _currentIndex

    private val _searchQuery = mutableStateOf("")
    val searchQuery: State<String> = _searchQuery


    fun addClock(city: String, timeZoneId: String){
        if(!_clocks.any{it.city == city}){
            _clocks.add(ClockItem(city, timeZoneId))
        }
    }

    fun updateCurrentIndex(index: Int){
        _currentIndex.value = index
    }

    fun updateSearchQuery(query: String){
        _searchQuery.value = query
    }

    fun removeClock(clockItem: ClockItem){
        _clocks.remove(clockItem)
    }

    fun getFilteredCities():List<Pair<String, TimeZone>>{
        return cites.filter { it.name.contains(_searchQuery.value, ignoreCase = true) }.map { it.name to it.timeZoneId }
    }
}