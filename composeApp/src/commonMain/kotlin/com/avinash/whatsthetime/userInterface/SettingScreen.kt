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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import org.jetbrains.compose.resources.painterResource
import whatsthetime.composeapp.generated.resources.Res
import whatsthetime.composeapp.generated.resources.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
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

@Composable
fun SettingsScreen(navController: NavController, userLocalCity: String, userLocalCountry: String, TimeZone: String, viewModel: WorldClockViewModel) {

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
    Box(modifier = Modifier
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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.35f)
                .background(color = backgroundColor)
        ) {
            IconButton(
                onClick = { navController.navigateUp() },
                modifier = Modifier
                    .padding(30.dp)
                    .align(Alignment.TopStart)
                    .size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFFFF69B4),
                    modifier = Modifier.size(44.dp)
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isEditingName) {
                    BasicTextField(
                        value = userName,
                        onValueChange = {
                            userName = it },
                        textStyle = androidx.compose.ui.text.TextStyle(
                            color = textColor,
                            fontSize = 34.sp,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        ),
                        modifier = Modifier
                            .background(Color.Transparent)
                            .padding(8.dp)
                    )
                } else {
                    Text(
                        text = userName,
                        fontSize = 34.sp,
                        color = textColor,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        modifier = Modifier.pointerInput(Unit) {
                            detectTapGestures(
                                onDoubleTap = {
                                    isEditingName = true
                                }
                            )
                        }
                    )
                }
                Text(
                    text = "$userLocalCity, $userLocalCountry",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(46.dp))

                if (!profileImageUri.isNullOrEmpty()) {
                    println("Displaying image from URI: $profileImageUri")
                    CoilImage(
                        imageModel = { profileImageUri },
                        imageOptions = ImageOptions(
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.Center
                        ),
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color.White, CircleShape)
                            .clickable { pickerLauncher.launch() }
                    )
                } else {
                    Image(
                        painter = painterResource(Res.drawable.Shape),
                        contentDescription = "Profile Picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color.White, CircleShape)
                            .clickable { pickerLauncher.launch() }
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.65f)
                .align(Alignment.BottomCenter)
                .background(Color.White)
        ) {
            Text(
                text = "Settings",
                fontSize = 20.sp,
                modifier = Modifier.padding(start = 16.dp, top = 48.dp, bottom = 16.dp)
            )

            SettingsItem(
                title = "Clock Type",
                showArrow = true,
                onClick = { /* Handle clock type click */ }
            )

            SettingsItem(
                title = "Date Format",
                showArrow = true,
                onClick = { /* Handle date format click */ }
            )

            SettingsItem(
                title = "24-Hour Time",
                showToggle = true,
                isToggled = !isTwelveHourFormat,
                onToggleChange = {
                    viewModel.updateTwelveHourFormat(!it)
                }
            )
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
    onToggleChange: ((Boolean) -> Unit)? = null
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        shape = RoundedCornerShape(12.dp),
        elevation = 0.dp,
        color = Color(0xFFF5F6FA)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .size(50.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 16.sp
            )
            when {
                showArrow -> Icon(
                    imageVector = Icons.Default.ArrowForward, // Make sure to add this resource
                    contentDescription = "Navigate",
                    tint = Color.Gray
                )
                showToggle -> Switch(
                    checked = isToggled,
                    onCheckedChange = { onToggleChange?.invoke(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF34C759)
                    )
                )
            }
        }
    }
}
