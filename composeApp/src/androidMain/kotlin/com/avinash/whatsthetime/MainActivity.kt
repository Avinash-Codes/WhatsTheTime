package com.avinash.whatsthetime

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.tooling.preview.Preview
import com.avinash.whatsthetime.api.LocationClient
import com.avinash.whatsthetime.api.createHttpClient
import okhttp3.OkHttp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            App(
                prefs = remember {
                    createDataStore(applicationContext)
                },
                client = remember{
                    LocationClient(createHttpClient(io.ktor.client.engine.okhttp.OkHttp.create()))
                }
            )
        }
    }
}
//
//actual class ScreenConfigurationImpl(private val context: Context) : ScreenConfiguration {
//    private val metrics: DisplayMetrics = context.resources.displayMetrics
//
//    actual override val screenWidth: Int
//        get() = metrics.widthPixels
//
//    actual override val screenHeight: Int
//        get() = metrics.heightPixels
//
//    actual override val screenDensity: Float
//        get() = metrics.density
//}

//actual fun createScreenConfiguration(context: Any?): ScreenConfiguration {
//    require(context is Context) { "Context is required for Android implementation" }
//    return ScreenConfigurationImpl(context)
//}



