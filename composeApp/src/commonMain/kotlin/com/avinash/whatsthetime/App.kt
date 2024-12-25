package com.avinash.whatsthetime

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalMapOf
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.romainguy.kotlin.math.degrees
import dev.romainguy.kotlin.math.min
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import kotlinx.datetime.IllegalTimeZoneException
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import whatsthetime.composeapp.generated.resources.Res
import whatsthetime.composeapp.generated.resources.compose_multiplatform
import whatsthetime.composeapp.generated.resources.dot
import whatsthetime.composeapp.generated.resources.hour
import whatsthetime.composeapp.generated.resources.minute
import whatsthetime.composeapp.generated.resources.sec
import whatsthetime.composeapp.generated.resources.white_clock
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

fun currentDateAndTime(location: String): Pair<String?, String?> {
    fun LocalTime.formatted() = "$hour:$minute:$second"

    return try {
        val time = Clock.System.now()
        val zone = TimeZone.of(location)
        val localDateTime = time.toLocalDateTime(zone)
        val timeString = localDateTime.time.formatted()
        val dateString = localDateTime.date.toString()
        dateString to timeString
    } catch (ex: IllegalTimeZoneException) {
        null to null
    }
}

@Composable
@Preview
fun App() {
    MaterialTheme {
        val currentTime = remember { mutableStateOf(currentDateAndTime("Asia/Kolkata")) }

        LaunchedEffect(Unit) {
            while (true) {
                currentTime.value = currentDateAndTime("Asia/Kolkata")
                delay(1000)
            }
        }

        val (date, time) = currentTime.value
        val (hours, minutes, seconds) = time?.split(":")?.map { it.toInt() } ?: listOf(0, 0, 0)

        val hourRotation = (hours % 12) * 30f + minutes * 0.5f
        val minuteRotation = minutes * 6f + seconds * 0.1f
        val secondRotation = seconds * 6f

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color(0xFFE7EEFB), Color(0xFFFFFFFF)))),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier.size(350.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Clock Face
                    Canvas(modifier = Modifier.fillMaxSize()) {

                        val circleCenter = center
                        val outerCircleRadius = size.minDimension / 2f
                        val littleLine = outerCircleRadius * 0.05f

                        val outerCircleBrush = Brush.radialGradient(
                            listOf(
                                Color(0xFFE7EEFB),
                                Color(0xFFE7EEFB),
                            )
                        )

                        val innerCircleBrush = Brush.radialGradient(
                            listOf(
                                Color(0xFFEDF1FB),
                                Color(0xFFEDF1FB),
                                Color(0xFFEDF1FB),
                                Color(0xFFEDF1FB)
                            )
                        )

                        //the outer frame circle
                        drawCircle(
                            brush = outerCircleBrush,
                            radius = outerCircleRadius,
                            center = circleCenter
                        )

                        //it is the inner circle
                        drawCircle(
                            brush = innerCircleBrush,
                            radius = outerCircleRadius - 110,
                            center = circleCenter
                        )

                        val markerMargin =
                            littleLine * 0.9f // Adjust this value for the space you want between the circle and markers

                        // Draw markers
                        for (i in 0 until 60) {
                            val angle = i * 360f / 60
                            val radians = Math.toRadians(angle.toDouble())
                            val lineLength = if (i % 5 == 0) {
                                littleLine * 2 // Longer line for hour markers
                            } else {
                                littleLine // Shorter line for minute markers
                            }

                            val lineThickness = 4.5f

                            val start = Offset(
                                x = ((outerCircleRadius - markerMargin) * cos(radians) + circleCenter.x).toFloat(),
                                y = ((outerCircleRadius - markerMargin) * sin(radians) + circleCenter.y).toFloat()
                            )

                            val end = Offset(
                                x = ((outerCircleRadius - markerMargin - lineLength) * cos(radians) + circleCenter.x).toFloat(),
                                y = ((outerCircleRadius - markerMargin - lineLength) * sin(radians) + circleCenter.y).toFloat()
                            )

                            drawLine(
                                color = Color(0xFFFFFFFF),
                                start = start,
                                end = end,
                                strokeWidth = lineThickness.dp.toPx(),
                                cap = StrokeCap.Butt
                            )
                        }

                        val clockHands =
                            listOf(ClockHands.seconds, ClockHands.minutes, ClockHands.hours)

                        clockHands.forEach { clockHand ->
                            val eachDegree = 360f / 60f

                            val angleInDegree = when (clockHand) {
                                ClockHands.seconds -> {
                                    seconds * eachDegree
                                }

                                ClockHands.minutes -> {
                                    (minutes + seconds / 60f) * eachDegree
                                }

                                ClockHands.hours -> {
                                    (60 * hours + minutes) * 30f / 60f
                                }
                            }

                            val lineLength = when (clockHand) {
                                ClockHands.seconds -> {
                                    outerCircleRadius.times(0.7f)
                                }

                                ClockHands.minutes -> {
                                    outerCircleRadius.times(0.8f)
                                }

                                ClockHands.hours -> {
                                    outerCircleRadius.times(0.5f)
                                }
                            }

                            val lineThickness = when (clockHand) {
                                ClockHands.seconds -> {
                                    3f
                                }

                                ClockHands.minutes -> {
                                    7f
                                }

                                ClockHands.hours -> {
                                    9f
                                }
                            }

                            val start = Offset(
                                x = circleCenter.x,
                                y = circleCenter.y
                            )

                            val end = Offset(
                                x = circleCenter.x,
                                y = lineLength + circleCenter.y
                            )

                            rotate(
                                angleInDegree - 180,
                                pivot = start
                            ){
                                drawLine(
                                    color = if(clockHand == ClockHands.seconds) Color(0xFFFF007F) else if(clockHand == ClockHands.minutes) Color(0xFF9FA7BC) else  Color.Black,
                                    start = center - Offset(0f, outerCircleRadius * 0.1f),
                                    end = center + Offset(0f, outerCircleRadius * 0.5f),
                                    strokeWidth = lineThickness.dp.toPx(),
                                    cap = StrokeCap.Round
                                )
                            }
                        }
                    }



                }

                Spacer(modifier = Modifier.height(16.dp))

                // Labels
                Text(text = "Chicago", style = MaterialTheme.typography.h4, color = Color(0xFFFF007F))
                Text(text = time ?: "--:--:--", style = MaterialTheme.typography.h5)
                Text(text = "United States", style = MaterialTheme.typography.body2, color = Color.Gray)

                Spacer(modifier = Modifier.height(32.dp))

                // Bottom Navigation
//                BottomNavigationBar()
            }
        }
    }
}




enum class ClockHands {
    hours, minutes, seconds
}