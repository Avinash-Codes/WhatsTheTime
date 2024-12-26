package com.avinash.whatsthetime.userInterface

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import com.avinash.whatsthetime.ClockHands
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import kotlinx.datetime.IllegalTimeZoneException
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
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

enum class ClockHands {
    hours, minutes, seconds
}

@Composable
fun AnalogWatch(timeZoneId: TimeZone) {
    val currentTime = rememberSaveable { mutableStateOf(com.avinash.whatsthetime.currentDateAndTime(
        timeZoneId.toString()
    )) }

    // Get initial rotations based on current time
    val (_, time) = currentTime.value
    val (hours, minutes, seconds) = time?.split(":")?.map { it.toInt() } ?: listOf(0, 0, 0)

    // Calculate initial positions (in degrees)
    val initialSecondRotation = (seconds * 6f) // 6 degrees per second
    val initialMinuteRotation = (minutes * 6f) + (seconds * 0.1f) // 6 degrees per minute + slight adjustment for seconds
    val initialHourRotation = (hours % 12 * 30f) + (minutes * 0.5f) // 30 degrees per hour + adjustment for minutes

    // Create animated rotation values with correct initial positions
    val infiniteTransition = rememberInfiniteTransition()

    val secondRotation by infiniteTransition.animateFloat(
        initialValue = initialSecondRotation,
        targetValue = initialSecondRotation + 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(60000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val minuteRotation by infiniteTransition.animateFloat(
        initialValue = initialMinuteRotation,
        targetValue = initialMinuteRotation + 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3600000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val hourRotation by infiniteTransition.animateFloat(
        initialValue = initialHourRotation,
        targetValue = initialHourRotation + 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(43200000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    LaunchedEffect(Unit) {
        while (true) {
            currentTime.value = com.avinash.whatsthetime.currentDateAndTime(timeZoneId.toString())
            delay(1000)
        }
    }

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

                    // Outer frame circle
                    drawCircle(
                        brush = outerCircleBrush,
                        radius = outerCircleRadius,
                        center = circleCenter
                    )

                    // Inner circle
                    drawCircle(
                        brush = innerCircleBrush,
                        radius = outerCircleRadius - 110,
                        center = circleCenter
                    )

                    val markerMargin = littleLine * 0.9f

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

                    val clockHands = listOf(ClockHands.seconds, ClockHands.minutes, ClockHands.hours)

                    clockHands.forEach { clockHand ->
                        val angleInDegree = when (clockHand) {
                            ClockHands.seconds -> secondRotation
                            ClockHands.minutes -> minuteRotation
                            ClockHands.hours -> hourRotation
                        }

                        val lineLength = when (clockHand) {
                            ClockHands.seconds -> outerCircleRadius.times(0.8f)
                            ClockHands.minutes -> outerCircleRadius.times(0.7f)
                            ClockHands.hours -> outerCircleRadius.times(0.5f)
                        }

                        val lineThickness = when (clockHand) {
                            ClockHands.seconds -> 3f
                            ClockHands.minutes -> 5f
                            ClockHands.hours -> 6f
                        }

                        rotate(
                            angleInDegree - 180,
                            pivot = center
                        ) {
                            drawLine(
                                color = if(clockHand == ClockHands.seconds) Color(0xFFFF007F)
                                else if(clockHand == ClockHands.minutes) Color(0xFF9FA7BC)
                                else Color.Black,
                                start = center - Offset(0f, outerCircleRadius * 0.1f),
                                end = center + Offset(0f, lineLength),
                                strokeWidth = lineThickness.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                        }
                    }

                    // Center circle
                    drawCircle(
                        color = Color(0xFFFF007F),
                        radius = 20f,
                        center = circleCenter
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Labels
            Text(text ="Chicago", style = MaterialTheme.typography.h4, color = Color(0xFFFF007F))
            Text(text = time ?: "--:--:--", style = MaterialTheme.typography.h5)
            Text(text = "United States", style = MaterialTheme.typography.body2, color = Color.Gray)

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}