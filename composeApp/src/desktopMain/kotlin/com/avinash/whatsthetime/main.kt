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
import java.awt.Toolkit

fun main(){
    val prefs = createDataStore {
        DATA_STORE_NAME
    }
    application {
        val state = rememberWindowState(
            position = WindowPosition(Alignment.Center)
        )
        Window(
            onCloseRequest = ::exitApplication,
            title = "WhatsTheTime",
            state = state
        ) {
            App(
                prefs = prefs,
                client = remember{
                    LocationClient(createHttpClient(io.ktor.client.engine.okhttp.OkHttp.create()))
                }
            )
        }
    }
}

//actual class ScreenConfigurationImpl : ScreenConfiguration {
//    override val screenWidth: Int
//        get() = Toolkit.getDefaultToolkit().screenSize.width
//
//    override val screenHeight: Int
//        get() = Toolkit.getDefaultToolkit().screenSize.height
//
//    override val screenDensity: Float
//        get() = Toolkit.getDefaultToolkit().screenResolution / 96.0f // DPI to density
//}


//
//actual fun createScreenConfiguration(context: Any?): ScreenConfiguration {
//    // Desktop does not need a context
//    return ScreenConfigurationImpl()
//}


