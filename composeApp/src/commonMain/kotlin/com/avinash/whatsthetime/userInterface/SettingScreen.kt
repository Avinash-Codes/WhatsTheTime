package com.avinash.whatsthetime.userInterface

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.avinash.whatsthetime.navigation.Screen
import kotlinx.datetime.Clock
import kotlinx.datetime.toLocalDateTime
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.IconButton
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import org.jetbrains.compose.resources.painterResource
import whatsthetime.composeapp.generated.resources.Res
import whatsthetime.composeapp.generated.resources.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.avinash.whatsthetime.getPlatform
//import com.avinash.whatsthetime.viewmodel.UserPreferences
import com.avinash.whatsthetime.viewmodel.WorldClockViewModel
import com.mohamedrejeb.calf.core.LocalPlatformContext
import com.mohamedrejeb.calf.io.getPath
import com.mohamedrejeb.calf.picker.FilePickerFileType
import com.mohamedrejeb.calf.picker.FilePickerSelectionMode
import com.mohamedrejeb.calf.picker.rememberFilePickerLauncher
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil3.CoilImage
import kotlinx.coroutines.launch
import whatsthetime.composeapp.generated.resources.user

@Composable
fun SettingsScreen(navController: NavController, userLocalCity: String, userLocalCountry: String, TimeZone: String, viewModel: WorldClockViewModel) {
    val platform = getPlatform()
    val isDesktop = "Java" in platform.name
    val isAndroid = "Android" in platform.name

    val scope = rememberCoroutineScope()
    val context = LocalPlatformContext.current
    var byteArray by remember { mutableStateOf<ByteArray?>(null) }
    val profileImageUri by viewModel.profileImageUri.collectAsState()
    var isEditingName by remember { mutableStateOf(false) }
    var userName by remember { mutableStateOf(viewModel.username.value) }
    val isTwelveHourFormat by viewModel.isTwelveHourFormat.collectAsState()

    val pickerLauncher = rememberFilePickerLauncher(
        type = FilePickerFileType.Image,
        selectionMode = FilePickerSelectionMode.Single,
        onResult = { files ->
            scope.launch {
                files.firstOrNull()?.let { file ->
                    try {
                        val uri = file.getPath(context)
                        if (uri != null) {
                            viewModel.saveProfileImageUri(uri)
                            println("Saved image URI: $uri")
                        } else {
                            println("Failed to get file path")
                        }
                    } catch (e: Exception) {
                        println("Error processing picked file: ${e.message}")
                    }
                }
            }
        }
    )

    val currentHour = Clock.System.now()
        .toLocalDateTime(kotlinx.datetime.TimeZone.of(TimeZone))
        .hour
    val isNightTime = currentHour in 21..23 || currentHour in 0..4
    val backgroundColor = if (isNightTime) Color(0xFF142550) else Color(0xFFE7EEFB)
    val textColor = if (isNightTime) Color.White else Color.Black

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    if (isEditingName) {
                        isEditingName = false
                        viewModel.updateUsername(userName)
                    }
                })
            }
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.4f)
                    .background(color = backgroundColor)
            ) {
                IconButton(
                    onClick = { navController.navigateUp() },
                    modifier = Modifier
                        .padding(if (isDesktop) 40.dp else 30.dp)
                        .size(if (isDesktop) 56.dp else 48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFFFF69B4),
                        modifier = Modifier.size(if (isDesktop) 52.dp else 44.dp)
                    )
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                       .padding(top = if (isDesktop) 100.dp else 80.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    if (isEditingName) {
                        BasicTextField(
                            value = userName,
                            onValueChange = { userName = it },
                            textStyle = androidx.compose.ui.text.TextStyle(
                                color = textColor,
                                fontSize = if (isDesktop) 42.sp else 34.sp,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                            ),
                            modifier = Modifier
                                .background(Color.Transparent)
                                .padding(12.dp)
                        )
                    } else {
                        Text(
                            text = userName,
                            fontSize = if (isDesktop) 42.sp else 34.sp,
                            color = textColor,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            modifier = Modifier.pointerInput(Unit) {
                                detectTapGestures(onDoubleTap = { isEditingName = true })
                            }
                        )
                    }
                    Text(
                        text = "$userLocalCity, $userLocalCountry",
                        fontSize = if (isDesktop) 18.sp else 14.sp,
                        color = Color.Gray
                    )
                }

                // Profile Image positioned at bottom center, overlapping the sections
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = if (isDesktop) 180.dp else 150.dp)
                        .offset(y = if (isDesktop) 30.dp else 10.dp)
                ) {
                    if (!profileImageUri.isNullOrEmpty()) {
                        CoilImage(
                            imageModel = { profileImageUri },
                            imageOptions = ImageOptions(
                                contentScale = ContentScale.Crop,
                                alignment = Alignment.Center
                            ),
                            modifier = Modifier
                                .size(if (isDesktop) 140.dp else 100.dp)
                                .clip(CircleShape)
                                .border(if (isDesktop) 3.dp else 2.dp, Color.White, CircleShape)
                                .clickable { pickerLauncher.launch() }
                        )
                    } else {
                        Image(
                            painter = painterResource(Res.drawable.user),
                            contentDescription = "Profile Picture",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(if (isDesktop) 140.dp else 100.dp)
                                .clip(CircleShape)
                                .border(if (isDesktop) 3.dp else 2.dp, Color.White, CircleShape)
                                .clickable { pickerLauncher.launch() }
                        )
                    }
                }
            }

            // Settings Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.6f)
                    .background(Color.White)
            ) {
                Spacer(modifier = Modifier.height(if (isDesktop) 80.dp else 60.dp))

                Text(
                    text = "Settings",
                    fontSize = if (isDesktop) 26.sp else 20.sp,
                    modifier = Modifier.padding(
                        start = if (isDesktop) 32.dp else 16.dp,
                        bottom = if (isDesktop) 24.dp else 16.dp
                    )
                )

                SettingsItem(
                    title = "Clock Type",
                    showArrow = true,
                    onClick = { /* Handle clock type click */ },
                    isDesktop = isDesktop
                )

                SettingsItem(
                    title = "Date Format",
                    showArrow = true,
                    onClick = { /* Handle date format click */ },
                    isDesktop = isDesktop
                )

                SettingsItem(
                    title = "24-Hour Time",
                    showToggle = true,
                    isToggled = !isTwelveHourFormat,
                    onToggleChange = { viewModel.updateTwelveHourFormat(!it) },
                    isDesktop = isDesktop
                )
            }
        }
    }
}

@Composable
private fun SettingsItem(
    title: String,
    showArrow: Boolean = false,
    showToggle: Boolean = false,
    isToggled: Boolean = false,
    onClick: (() -> Unit)? = null,
    onToggleChange: ((Boolean) -> Unit)? = null,
    isDesktop: Boolean = false
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = if (isDesktop) 32.dp else 16.dp,
                vertical = if (isDesktop) 12.dp else 8.dp
            )
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        shape = RoundedCornerShape(if (isDesktop) 16.dp else 12.dp),
        elevation = 0.dp,
        color = Color(0xFFF5F6FA)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(if (isDesktop) 24.dp else 16.dp)
                .height(if (isDesktop) 60.dp else 50.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = if (isDesktop) 18.sp else 16.sp
            )
            when {
                showArrow -> Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Navigate",
                    tint = Color.Gray,
                    modifier = Modifier.size(if (isDesktop) 28.dp else 24.dp)
                )
                showToggle -> Switch(
                    checked = isToggled,
                    onCheckedChange = { onToggleChange?.invoke(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF34C759)
                    ),
                    modifier = Modifier.scale(if (isDesktop) 1.2f else 1f)
                )
            }
        }
    }
}
