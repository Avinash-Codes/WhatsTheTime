package com.avinash.whatsthetime.userInterface

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.avinash.whatsthetime.dataclass.Cites
import com.avinash.whatsthetime.dataclass.cites

@Composable
fun CitySearchBar(onCitySelected: (Cites) -> Unit) {
    var query by rememberSaveable { mutableStateOf("") }
    val filteredCities = cites.filter { it.name.contains(query, ignoreCase = true) }

    Column{
        TextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Search for a city") },
            modifier = Modifier.fillMaxWidth()
        )

        LazyColumn{
            items(filteredCities){ city ->
                Text(
                    text = city.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onCitySelected(city) }
                        .padding(16.dp)
                )
            }
        }
    }
}


@Composable
fun CityClockApp(navController: NavController) {
    var selectedCity by rememberSaveable { mutableStateOf<Cites?>(null) }

    Column{
        CitySearchBar { city ->
            selectedCity = city
        }

        selectedCity?.let {
            AnalogWatch(it.timeZoneId)
        }
    }
}