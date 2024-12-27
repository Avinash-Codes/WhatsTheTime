package com.avinash.whatsthetime.userInterface

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.FloatingActionButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.avinash.whatsthetime.navigation.Screen
import com.avinash.whatsthetime.viewmodel.WorldClockViewModel
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import org.jetbrains.compose.resources.painterResource
import whatsthetime.composeapp.generated.resources.Res
import whatsthetime.composeapp.generated.resources.Shape
import whatsthetime.composeapp.generated.resources.add
import whatsthetime.composeapp.generated.resources.alarm
import whatsthetime.composeapp.generated.resources.minute
import whatsthetime.composeapp.generated.resources.sec
import whatsthetime.composeapp.generated.resources.setting
import whatsthetime.composeapp.generated.resources.settings


@Composable
fun MainLayout(navController: NavController, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ){
        content()
        CustomBottomNavigation(
            navController = navController,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun CustomBottomNavigation(navController: NavController, modifier: Modifier) {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route


    BottomNavigation(
        modifier = modifier.background(Color.Transparent).padding(bottom = 26.dp),
        backgroundColor = Color.Transparent,
        contentColor = Color.Gray,
        elevation = 0.dp
    ) {
        BottomNavigationItem(
            icon = {
                Icon(
                    painter = painterResource(Res.drawable.alarm),
                    contentDescription = "Clock",
                    modifier = Modifier.size(40.dp,40.dp).padding(bottom = 4.dp)
                )
            },
            label = { Text("Clock", fontSize = 12.sp ) },
            selected = currentRoute == Screen.HomeScreen.route,
            onClick = {
                if (currentRoute != Screen.HomeScreen.route) {
                    navController.navigate(Screen.HomeScreen.route) {
                        popUpTo(Screen.HomeScreen.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            selectedContentColor = Color(0xFF000000),
            unselectedContentColor = Color.Gray
        )

        BottomNavigationItem(
            icon = {
                Icon(
                    painter = painterResource(Res.drawable.Shape),
                    contentDescription = "List",
                    modifier = Modifier.size(30.dp,30.dp).padding(bottom = 4.dp)
                )
            },
            label = { Text("List", fontSize = 12.sp) },
            selected = currentRoute == Screen.ListScreen.route,
            onClick = {
                if (currentRoute != Screen.ListScreen.route) {
                    navController.navigate(Screen.ListScreen.route) {
                        popUpTo(Screen.HomeScreen.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            selectedContentColor = Color(0xFF000000),
            unselectedContentColor = Color.Gray
        )

        BottomNavigationItem(
            icon = {
                Icon(
                    painter = painterResource(Res.drawable.setting),
                    contentDescription = "Settings",
                    modifier = Modifier.size(40.dp,40.dp).padding(bottom = 4.dp),
                )
            },
            label = { Text("Settings", fontSize = 12.sp) },
            selected = currentRoute == Screen.SettingsScreen.route,
            onClick = {
                if (currentRoute != Screen.SettingsScreen.route) {
                    navController.navigate(Screen.SettingsScreen.route) {
                        popUpTo(Screen.HomeScreen.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            selectedContentColor = Color(0xFF000000),
            unselectedContentColor = Color.Gray
        )
    }

}

@Composable
fun HomeScreen(navController: NavController,userLocalCity: String, userLocalCountry: String,TimeZone: String,viewModel: WorldClockViewModel) {
    val clocks = viewModel.clocks
    val currentIndex = viewModel.currentIndex.value
    var currentTime by rememberSaveable { mutableStateOf(Clock.System.now().toString()) }

    LaunchedEffect(Unit){
        while(true){
            delay(1000)
            currentTime = Clock.System.now().toString()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8FF))
    ) {
//        AnalogWatch(
//            timeZoneId = TimeZone,
//            cityName = CityName,
//            countryName = CountryName
//        )

        if(clocks.isEmpty()){
            LocalClockDisplay(
                city = userLocalCity,
                country = userLocalCountry,
                timeZone = TimeZone,
                currentTime = currentTime.toString()
            )
        }else{
            HorizontalPager(
                state = rememberPagerState{clocks.size + 1}
            ) { page ->
                when(page){
                    0 -> LocalClockDisplay(
                        city = userLocalCity,
                        country = userLocalCountry,
                        timeZone = TimeZone,
                        currentTime = currentTime.toString()
                    )
                    else -> ClockDisplay(
                        clocks[page - 1],
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(26.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(17.dp, 40.dp, 17.dp, 17.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            FloatingActionButton(
                onClick = {
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

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
        ) {

        }
    }

}

@Composable
fun LocalClockDisplay(city: String, country: String, timeZone: String, currentTime: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = city,
            fontSize = 24.sp
        )
        Text(
            text = country,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        AnalogWatch(
            timeZoneId = timeZone,
            cityName = city,
            countryName = country,
        )
    }
}
