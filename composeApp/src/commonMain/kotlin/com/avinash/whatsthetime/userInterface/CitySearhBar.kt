package com.avinash.whatsthetime.userInterface

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.FloatingActionButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.avinash.whatsthetime.ScreenConfiguration
//import com.avinash.whatsthetime.ScreenConfigurationImpl
//import com.avinash.whatsthetime.createScreenConfiguration
import com.avinash.whatsthetime.currentTime
import com.avinash.whatsthetime.dataclass.Cites
import com.avinash.whatsthetime.dataclass.ClockItem
import com.avinash.whatsthetime.dataclass.cites
import com.avinash.whatsthetime.getPlatform
import com.avinash.whatsthetime.navigation.Screen
import com.avinash.whatsthetime.viewmodel.WorldClockViewModel
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun ListScreen(
    navController: NavController,
    viewModel: WorldClockViewModel
) {
    var isSearchVisible by rememberSaveable { mutableStateOf(false) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val searchResults by viewModel.searchResults.collectAsState()
    val isAddButtonVisible by rememberSaveable { mutableStateOf(true) }
    val isEditButtonVisible by rememberSaveable { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8FF))
    ){
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            if(!isSearchVisible) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(17.dp, 40.dp, 17.dp, 17.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    FloatingActionButton(
                        onClick = {
                            isSearchVisible = true
                        },
                        contentColor = Color(0xFFFFFFFF),
                        backgroundColor = Color(0xFFD81F72),
                        modifier = Modifier
                            .clickable { },
                        elevation = FloatingActionButtonDefaults.elevation(10.dp)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add",
                        )
                    }

                    FloatingActionButton(
                        onClick = {

                        },
                        backgroundColor = Color(0xFFFFFFFF),
                        modifier = Modifier
                            .clickable { },
                        elevation = FloatingActionButtonDefaults.elevation(10.dp)

                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = Color(0xFFD81F72)
                        )
                    }

                }
            }

            //Top Bar
            if(isSearchVisible){
                Spacer(modifier = Modifier.height(26.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    IconButton(
                        onClick = { navController.navigateUp() }
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFFD81F72),
                            modifier = Modifier.size(44.dp)
                        )
                    }
                }
            }

//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(16.dp),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.Top
//            ) {
//                IconButton(
//                    onClick = { navController.navigateUp() }
//                ) {
//                    Icon(
//                        Icons.Default.ArrowBack,
//                        contentDescription = "Back",
//                        tint = Color(0xFFD81F72)
//                    )
//                }
//
////                AnimatedVisibility(
////                    visible = !isSearchVisible,
////                    enter = fadeIn(),
////                    exit = fadeOut()
////                ) {
////                    IconButton(
////                        onClick = { isSearchVisible = true }
////                    ) {
////                        Icon(
////                            Icons.Default.Search,
////                            contentDescription = "Search",
////                            tint = Color(0xFFD81F72)
////                        )
////                    }
////                }
//            }

            Spacer(modifier = Modifier.height(16.dp))
            AnimatedVisibility(
                visible = isSearchVisible,
                enter = slideInHorizontally() + fadeIn(),
                exit = slideOutHorizontally() + fadeOut()
            ) {
                SearchBar(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        viewModel.updateSearchQuery(it)
                    },
                    modifier = Modifier.padding(16.dp)
                )
            }

            AnimatedVisibility(
                visible = !isSearchVisible || searchQuery.isNotEmpty(),
                enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
                exit = slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
            ) {
                LazyColumn {
                    if (searchQuery.isNotEmpty()) {
                        if (searchResults.isEmpty()) {
                            item {
                                Text(
                                    text = "No cities found",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    textAlign = TextAlign.Center,
                                    color = Color.Gray
                                )
                            }
                        } else {
                            items(searchResults) { (city, timezoneId) ->
                                ClockListItem(
                                    clockItem = ClockItem(
                                        city = city,
                                        timeZoneId = timezoneId.toString()
                                    ),
                                    onAddClick = {
                                        viewModel.addClock(city, timezoneId.toString())
                                        isSearchVisible = false
                                        searchQuery = ""
                                        navController.navigate(Screen.HomeScreen.route)
                                    },
                                    viewModel = viewModel

                                )
                            }
                        }
                    } else {
                        items(viewModel.clocks) { clockItem ->
                            ClockListItem(
                                clockItem = clockItem,
                                onEditClick = {
                                    viewModel.removeClock(clockItem)
                                },
                                showEditIcon = true,
                                viewModel = viewModel
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun ClockDisplay(clockItem: ClockItem,viewModel: WorldClockViewModel) {
    var currentTime by rememberSaveable { mutableStateOf(clockItem.timestamp.toString()) }
    LaunchedEffect(Unit){
        while(true){
            delay(1000)
            currentTime = clockItem.timestamp.toString()
        }
    }

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
//        Text(
//            text = clockItem.city,
//            style = MaterialTheme.typography.h5,
//            modifier = Modifier.padding(bottom = 8.dp)
//        )

        AnalogWatch(
            timeZoneId = clockItem.timeZoneId,
            cityName = clockItem.city,
            countryName = "",
            viewModel = viewModel
        )

    }
}


@Composable
fun ClockListItem(
    clockItem: ClockItem,
    onAddClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    showEditIcon: Boolean = false,
    viewModel: WorldClockViewModel
) {
    val platform = getPlatform()
    println("Platform: ${platform.name}")
    var android = false
    var desktop = false
    if ("Android" in platform.name) android = true
    if ("Java" in platform.name) desktop = true

    val localTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val targetTime = Clock.System.now().toLocalDateTime(TimeZone.of(clockItem.timeZoneId))
    val timeDifference = targetTime.hour - localTime.hour
    val timeDifferenceMinutes = targetTime.minute - localTime.minute
    val timeDifferenceString = if (timeDifference >= 0) {
        "+${timeDifference}:${timeDifferenceMinutes.toString().padStart(2, '0')}"
    } else {
        "${timeDifference}:${timeDifferenceMinutes.toString().padStart(2, '0')}"
    }

    val dayInfo = when {
        targetTime.date == localTime.date -> "Today"
        targetTime.date > localTime.date -> "Tomorrow"
        else -> "Yesterday"
    }

    Row(
        modifier = if (android) {
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .background(Color(0xFFd1def0), RoundedCornerShape(12.dp))
                .padding(16.dp)
                .size(72.dp)
        } else {
            // Desktop-specific styling
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 48.dp, vertical = 24.dp)
                .background(Color(0xFFd1def0), RoundedCornerShape(16.dp))
                .padding(24.dp)
                .height(160.dp)
        },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = if (android) {
                    Modifier
                        .size(52.dp)
                        .background(Color(0xFFF1F1F1), CircleShape)
                } else {
                    // Desktop-specific watch size
                    Modifier
                        .size(120.dp)
                        .background(Color(0xFFF1F1F1), CircleShape)
                        .clip(CircleShape)
                }
            ) {
                SmallAnalogWatch(
                    timeZoneId = clockItem.timeZoneId,
                    cityName = clockItem.city,
                    countryName = "",
                    viewModel = viewModel
                )
            }

            Spacer(modifier = if (android) Modifier.width(16.dp) else Modifier.width(32.dp))

            Column {
                Text(
                    text = clockItem.city,
                    style = if (android) {
                        MaterialTheme.typography.subtitle1
                    } else {
                        MaterialTheme.typography.h6
                    },
                    fontWeight = FontWeight.Medium
                )
                currentTime(clockItem.timeZoneId)?.let {
                    Text(
                        text = it,
                        style = if (android) {
                            MaterialTheme.typography.caption
                        } else {
                            MaterialTheme.typography.subtitle1
                        },
                        color = Color.Gray
                    )
                }
                Text(
                    text = "$timeDifferenceString $dayInfo",
                    style = if (android) {
                        MaterialTheme.typography.caption
                    } else {
                        MaterialTheme.typography.subtitle1
                    },
                    color = Color.Gray
                )
            }
        }

        Row {
            if (showEditIcon) {
                IconButton(onClick = onEditClick) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = Color(0xFFD81F72),
                        modifier = if (android) Modifier.size(24.dp) else Modifier.size(32.dp)
                    )
                }
            } else {
                IconButton(onClick = onAddClick) {
                    Icon(
                        Icons.Default.AddCircle,
                        contentDescription = "Add",
                        tint = Color(0xFFD81F72),
                        modifier = if (android) Modifier.size(32.dp) else Modifier.size(48.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SearchBar(value: String, onValueChange: (String) -> Unit,modifier: Modifier) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 8.dp)
            .background(Color.White, RoundedCornerShape(32.dp)),
        placeholder = { Text("Search cities...", fontSize = 20.sp) },
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = "Search",
                tint = (Color.Gray)
            )
        },
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = Color.Gray,
            textColor = Color.Black,
        ),
        singleLine = true,
        textStyle = MaterialTheme.typography.subtitle1
    )
}