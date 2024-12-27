package com.avinash.whatsthetime

import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import com.avinash.whatsthetime.api.LocationClient
import com.avinash.whatsthetime.api.createHttpClient
import io.ktor.client.engine.darwin.Darwin

fun MainViewController() = ComposeUIViewController {
    App(
        client =
        remember{
            LocationClient(createHttpClient(Darwin.create()))
        }
    )
}