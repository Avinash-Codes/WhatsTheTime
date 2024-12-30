package com.avinash.whatsthetime

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.avinash.whatsthetime.api.LocationClient
import com.avinash.whatsthetime.navigation.Screen
import com.avinash.whatsthetime.userInterface.HomeScreen
import com.avinash.whatsthetime.userInterface.ListScreen
import com.avinash.whatsthetime.userInterface.MainLayout
import com.avinash.whatsthetime.userInterface.SettingsScreen
import com.avinash.whatsthetime.viewmodel.WorldClockViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.IllegalTimeZoneException
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.ui.tooling.preview.Preview
import util.NetworkError
import util.onError
import util.onSuccess
import kotlin.math.cos
import kotlin.math.sin

fun currentDateAndTime(location: String): Pair<String?, String?> {
    fun LocalTime.formatted() = "$hour:$minute:$second"

    return try {
        val time = Clock.System.now()
        val zone = TimeZone.of(location)
        println("Timezone: $zone Time: $time.")
        val localDateTime = time.toLocalDateTime(zone)
        val timeString = localDateTime.time.formatted()
        val dateString = localDateTime.date.toString()
        dateString to timeString
    } catch (ex: IllegalTimeZoneException) {
        null to null
    }
}

fun currentTime(location: String): String? {
    return currentDateAndTime(location).second
}

@Composable
@Preview
fun App(prefs:DataStore<Preferences>,client: LocationClient) {
    MaterialTheme {
        var userLocalCity by rememberSaveable{ mutableStateOf("") }
        var userLocalCountry by rememberSaveable{ mutableStateOf("") }
        var isLoading by rememberSaveable{ mutableStateOf(false) }
        var errorMessage by rememberSaveable{ mutableStateOf<NetworkError?>(null) }

        LaunchedEffect(Unit) {
            isLoading = true
            client.getUserLocation("")
                .onSuccess { (city, country) ->
                    userLocalCity = city ?: ""
                    userLocalCountry = country ?: ""
                    errorMessage = null
                }
                .onError { error ->
                    errorMessage = error
                }
            isLoading = false
        }

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            if (errorMessage != null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Error: $errorMessage")
                }
            } else {
//                Text(text = "City: $userLocalCity")
//                Text(text = "Country: $userLocalCountry")
            }

        }
        val navController = rememberNavController()
        val viewModel = WorldClockViewModel(prefs)
//        val viewModel2 = UserPreferences(prefs)
        MainLayout(navController = navController) {
            NavHost(navController = navController, startDestination = Screen.HomeScreen.route) {
                composable(Screen.HomeScreen.route) { HomeScreen(navController,userLocalCity, userLocalCountry, TimeZone.currentSystemDefault().toString(), viewModel) }
                composable(Screen.ListScreen.route) { ListScreen(navController, viewModel) }
                composable(Screen.SettingsScreen.route) { SettingsScreen(navController,userLocalCity, userLocalCountry, TimeZone.currentSystemDefault().toString(),viewModel) }
            }
        }
    }
}

enum class ClockHands {
    hours, minutes, seconds
}