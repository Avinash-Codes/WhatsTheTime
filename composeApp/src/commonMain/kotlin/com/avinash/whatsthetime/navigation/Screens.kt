package com.avinash.whatsthetime.navigation

import kotlinx.serialization.Serializable

sealed interface Screen {
    val route: String

    @Serializable
    data object HomeScreen : Screen {
        override val route = "home"
    }

    @Serializable
    data object SettingsScreen : Screen {
        override val route = "settings"
    }

    @Serializable
    data object ListScreen : Screen {
        override val route = "list"
    }
}