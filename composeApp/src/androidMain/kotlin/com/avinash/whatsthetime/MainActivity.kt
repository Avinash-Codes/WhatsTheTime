package com.avinash.whatsthetime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
                client = remember{
                    LocationClient(createHttpClient(io.ktor.client.engine.okhttp.OkHttp.create()))
                }
            )
        }
    }
}

