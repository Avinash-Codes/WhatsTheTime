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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.avinash.whatsthetime.currentTime
import com.avinash.whatsthetime.dataclass.Cites
import com.avinash.whatsthetime.dataclass.ClockItem
import com.avinash.whatsthetime.dataclass.cites
import com.avinash.whatsthetime.navigation.Screen
import com.avinash.whatsthetime.viewmodel.WorldClockViewModel
import kotlinx.coroutines.delay

@Composable
fun ListScreen(
    navController: NavController,
    viewModel: WorldClockViewModel
) {
    var isSearchVisible by rememberSaveable { mutableStateOf(false) }
    var searchQuery by rememberSaveable { mutableStateOf("") }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8FF))
    ){
        Column(
            modifier = Modifier.fillMaxSize()
        ){
            //Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                IconButton(
                    onClick = {navController.navigateUp()}
                ){
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFFD81F72)
                    )
                }

                AnimatedVisibility(
                    visible = !isSearchVisible,
                    enter = fadeIn(),
                    exit = fadeOut()
                ){
                    IconButton(
                        onClick = {isSearchVisible = true}
                    ){
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color(0xFFD81F72)
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = isSearchVisible,
                enter = slideInHorizontally() + fadeIn(),
                exit = slideOutHorizontally() + fadeOut()
            ){
                SearchBar(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.padding(16.dp)
                )
            }

            AnimatedVisibility(
                visible = !isSearchVisible || searchQuery.isNotEmpty(),
                enter = slideInHorizontally(initialOffsetX = {it}) + fadeIn(),
                exit = slideOutHorizontally(targetOffsetX = {-it}) + fadeOut()
            ){
                LazyColumn {
                    if(searchQuery.isNotEmpty()){
                        items(viewModel.getFilteredCities()) { (city, timezoneId) ->
                            currentTime(timezoneId.toString())?.let {
                                ClockItem(city, timezoneId.toString(),
                                    it
                                )
                            }?.let {
                                ClockListItem(
                                    clockItem = it,
                                    onAddClick = {
                                        viewModel.addClock(city, timezoneId.toString())
                                        isSearchVisible = false
                                        searchQuery = ""
                                        navController.navigate(Screen.HomeScreen.route)
                                    }
                                )
                            }
                        }
                    }else{
                        items(viewModel.clocks) { clockItem ->
                            ClockListItem(
                                clockItem = clockItem,
                                onEditClick = {
                                    // Handle edit action
                                },
                                showEditIcon = true
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun ClockDisplay(clockItem: ClockItem) {
    var currentTime by rememberSaveable { mutableStateOf(clockItem.timestamp.toString()) }
    LaunchedEffect(Unit){
        while(true){
            delay(1000)
            currentTime = clockItem.timestamp.toString()
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = clockItem.city,
            style = MaterialTheme.typography.h5,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        AnalogWatch(
            timeZoneId = clockItem.timeZoneId,
            cityName = clockItem.city,
            countryName = "",

        )
    }
}
@Composable
fun ClockListItem(
    clockItem: ClockItem,
    onAddClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    showEditIcon: Boolean = false
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(Color(0xFFF1F1F1), RoundedCornerShape(12.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ){
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFFF1F1F1), CircleShape),
            ){
                AnalogWatch(
                    timeZoneId = clockItem.timeZoneId,
                    cityName = clockItem.city,
                    countryName = "",
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = clockItem.city,
                    style = MaterialTheme.typography.subtitle1,
                    fontWeight = FontWeight.Medium
                )
                currentTime(clockItem.timeZoneId)?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.caption,
                        color = Color.Gray
                    )
                }
            }
        }

        Row {
            if (showEditIcon) {
                IconButton(onClick = onEditClick) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = Color(0xFFD81F72)
                    )
                }
            } else {
                IconButton(onClick = onAddClick) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add",
                        tint = Color(0xFFD81F72)
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
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(4.dp),
        placeholder = { Text("Search cities...") },
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = "Search",
                tint = Color(0xFFD81F72)
            )
        },
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = Color(0xFFD81F72)
        ),
        singleLine = true
    )
}