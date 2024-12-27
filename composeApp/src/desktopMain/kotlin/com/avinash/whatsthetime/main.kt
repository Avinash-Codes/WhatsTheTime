package com.avinash.whatsthetime

import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.avinash.whatsthetime.api.LocationClient
import com.avinash.whatsthetime.api.createHttpClient

fun main() = application {
    val state = rememberWindowState(
        size = DpSize(400.dp, 300.dp),
        position = WindowPosition(Alignment.Center)
    )
    Window(
        onCloseRequest = ::exitApplication,
        title = "WhatsTheTime",
        state = state
    ) {
        App(
            client = remember{
                LocationClient(createHttpClient(io.ktor.client.engine.okhttp.OkHttp.create()))
            }
        )
    }
}