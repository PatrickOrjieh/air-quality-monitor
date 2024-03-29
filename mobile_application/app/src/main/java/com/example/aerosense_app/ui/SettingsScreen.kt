package com.example.aerosense_app.ui

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.aerosense_app.FirebaseViewModel
import com.example.aerosense_app.R
import com.example.aerosense_app.Screen
import com.example.aerosense_app.SettingsRequest
import com.example.aerosense_app.api.Repository
import com.example.aerosense_app.ui.components.NavBar
import com.example.aerosense_app.ui.components.SelectionDropDown


//Github copilot used when writing some of this code


@Composable
fun Settings(navController: NavHostController, repository: Repository, firebaseModel: FirebaseViewModel){
    var connected by remember { mutableStateOf(true) }
    var battery by remember { mutableIntStateOf(0) }
    var sound by remember { mutableStateOf(true) }
    var notificationFrequency by remember { mutableStateOf("") }
    val statuses = arrayOf("Only when critical", "Dangerous or below", "Moderate or below")

    battery = 65
    var vibration: Boolean = true

    // Obtain the token from the ViewModel
    val token = firebaseModel.firebaseToken
    Log.d("Token", "Token: $token")

// Check if the token is not null
    if (!token.isNullOrBlank()) {
        // Use the token to make the API call
        repository.fetchSettings(token,
            onSuccess = { settingsRequest ->
                if (settingsRequest != null) {
                    sound = settingsRequest.sound
                }
                if (settingsRequest != null) {
                    vibration = settingsRequest.vibration
                }
                if (settingsRequest != null) {
                    notificationFrequency = settingsRequest.notificationFrequency
                }

                Log.d("Check settings data", "Settings data: $settingsRequest")
            },
            onError = { errorMessage ->
                Log.d("Check Settings Data", "Error: $errorMessage")
            }
        )
    } else {
        Log.d("SettingsError", "Error: Firebase token is null or blank")
    }

    NavBar(navController)

    Text(
        text = "Settings",
        color = Color(0xff1e1e1e),
        textAlign = TextAlign.Center,
        style = TextStyle(
            fontSize = 40.sp,
            fontWeight = FontWeight.Medium),
        modifier = Modifier.padding(top = 80.dp))

    Image(
        painter = painterResource(id = R.drawable.separator),
        contentDescription = "Vector 38",
        modifier = Modifier
            .requiredWidth(width = 400.dp)
            .offset(y = -200.dp))

    //Device Management
    Box(
    ) {

        Text(
            text = "Device Management",
            color = Color(0xff1e1e1e),
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier
                .padding(top = 160.dp)
                .padding(start = 5.dp)
        )

        //Device connection status
        Row(
            modifier = Modifier
                .padding(top = 190.dp)
                .padding(start = 5.dp)
        ) {

            Box(
                modifier = Modifier
                    // .offset(y = -115.dp, x = -80.dp)
                    .requiredWidth(width = 180.dp)
                    .requiredHeight(height = 40.dp)
                    .clip(shape = RoundedCornerShape(6.dp))
                    .background(color = Color.White)
                    .border(
                        border = BorderStroke(4.dp, Color.LightGray),
                        shape = RoundedCornerShape(6.dp)
                    )
            ) {

                Text(
                    text = "Pair Status",
                    textAlign = TextAlign.Center,
                    color = Color(0xff1e1e1e),
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.padding(start = 35.dp, top = 5.dp)
                )


            }

            if(connected) {
                Text(
                    text = "Connected",
                    color = Color(0xff1e1e1e),
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier
                        .padding(top = 5.dp)
                        .padding(start = 20.dp)
                )

                Image(
                    painter = painterResource(id = R.drawable.tick),
                    contentDescription = "check",
                    modifier = Modifier
                        .requiredWidth(width = 45.dp)
                        .requiredHeight(height = 45.dp)
                        // .offset(y = 5.dp, x = 10.dp)
                        .padding(start = 10.dp)
                )
            }
            else{
                Text(
                    text = "Not Connected",
                    color = Color(0xff1e1e1e),
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier
                        .padding(top = 5.dp)
                        .padding(start = 20.dp)
                )
            }

        }

        //Battery Level
        Row(
            modifier = Modifier
                .padding(top = 245.dp)
                .padding(start = 5.dp)
        ) {

            Box(
                modifier = Modifier
                    // .offset(y = -115.dp, x = -80.dp)
                    .requiredWidth(width = 180.dp)
                    .requiredHeight(height = 40.dp)
                    .clip(shape = RoundedCornerShape(6.dp))
                    .background(color = Color.White)
                    .border(
                        border = BorderStroke(4.dp, Color.LightGray),
                        shape = RoundedCornerShape(6.dp)
                    )
            ) {

                Text(
                    text = "Battery Level",
                    textAlign = TextAlign.Center,
                    color = Color(0xff1e1e1e),
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.padding(start = 30.dp, top = 5.dp)
                )


            }

            Text(
                text = "$battery%",
                color = Color(0xff1e1e1e),
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier
                    .padding(top = 5.dp)
                    .padding(start = 20.dp)
            )

        }
    }

    Image(
        painter = painterResource(id = R.drawable.separator),
        contentDescription = "Vector 38",
        modifier = Modifier
            .requiredWidth(width = 400.dp)
            .offset(y = -45.dp)
    )

    Box(
        modifier = Modifier.padding(top = 145.dp)
    ) {

        Text(
            text = "Notification Settings",
            color = Color(0xff1e1e1e),
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier
                .padding(top = 170.dp)
                .padding(start = 5.dp)
        )

        //Device connection status
        Row(
            modifier = Modifier
                .padding(top = 220.dp)
                .padding(start = 5.dp)
        ) {

            Box(
                modifier = Modifier
                    // .offset(y = -115.dp, x = -80.dp)
                    .requiredWidth(width = 180.dp)
                    .requiredHeight(height = 40.dp)
                    .clip(shape = RoundedCornerShape(6.dp))
                    .background(color = Color.White)
                    .border(
                        border = BorderStroke(4.dp, Color.LightGray),
                        shape = RoundedCornerShape(6.dp)
                    )
            ) {

                Text(
                    text = "Sound",
                    textAlign = TextAlign.Center,
                    color = Color(0xff1e1e1e),
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.padding(start = 60.dp, top = 5.dp)
                )
            }

            //radio buttons
            Box(
                modifier = Modifier
                    .padding(start = 10.dp)
                    .selectableGroup()
            ) {
                RadioButton(
                    selected = sound,
                    onClick = {
                              sound = true

                        val requestBody = SettingsRequest(notificationFrequency, vibration, sound)

                        if (token != null) {
                            repository.updateUserSettings(token, requestBody,
                                onSuccess = { settingsResponse ->

                                    Log.d("Settings", "Success: $settingsResponse")
                                },
                                onError = { errorMessage ->
                                    Log.d("Settings", "Error: $errorMessage")
                                }
                            )
                        }
                    },
                    modifier = Modifier
                )
                Text(
                    text = "ON",
                    color = Color(0xff1e1e1e),
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium),
                    modifier = Modifier
                        .padding(start = 40.dp)
                        .padding(top = 10.dp))

                RadioButton(
                    selected = !sound,
                    onClick = { sound = false

                        val requestBody = SettingsRequest(notificationFrequency, vibration, sound)

                        if (token != null) {
                            repository.updateUserSettings(token, requestBody,
                                onSuccess = { settingsResponse ->

                                    Log.d("Settings", "Success: $settingsResponse")
                                },
                                onError = { errorMessage ->
                                    Log.d("Settings", "Error: $errorMessage")
                                }
                            )
                        }

                              },
                    modifier = Modifier
                        .padding(start = 70.dp))

                Text(
                    text = "OFF",
                    color = Color(0xff1e1e1e),
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium),
                    modifier = Modifier
                        .padding(start = 110.dp)
                        .padding(top = 10.dp))

            }

        }

        Row(
            modifier = Modifier
                .padding(top = 280.dp)
                .padding(start = 5.dp)
        ) {

            Box(
                modifier = Modifier
                    .padding(top = 20.dp)
                    .requiredWidth(width = 180.dp)
                    .requiredHeight(height = 40.dp)
                    .clip(shape = RoundedCornerShape(6.dp))
                    .background(color = Color.White)
                    .border(
                        border = BorderStroke(4.dp, Color.LightGray),
                        shape = RoundedCornerShape(6.dp)
                    )
            ) {

                Text(
                    text = "Frequency",
                    textAlign = TextAlign.Center,
                    color = Color(0xff1e1e1e),
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.padding(start = 40.dp, top = 5.dp)
                )


            }

            Log.d("Before passing to dropdown", "Notification frequency: $notificationFrequency")
            if(notificationFrequency != ""){
                SelectionDropDown(statuses, notificationFrequency, sound, token, repository, onItemSelected = { notificationFrequency = it })
            }

        }

    }

    Image(
        painter = painterResource(id = R.drawable.separator),
        contentDescription = "Vector 38",
        modifier = Modifier
            .requiredWidth(width = 400.dp)
            .padding(top = 355.dp)
    )

    //Account Settings
    Box(
        modifier = Modifier.padding(top = 370.dp)
    ) {

        Text(
            text = "Account Settings",
            color = Color(0xff1e1e1e),
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier
                .padding(top = 160.dp)
                .padding(start = 5.dp)
        )


        Row(
            modifier = Modifier
                .padding(top = 190.dp)
                .padding(start = 5.dp)
        ) {

            Box(
                modifier = Modifier
                    .requiredWidth(width = 180.dp)
                    .requiredHeight(height = 40.dp)
                    .clip(shape = RoundedCornerShape(6.dp))
                    .background(color = Color.White)
                    .border(
                        border = BorderStroke(4.dp, Color.LightGray),
                        shape = RoundedCornerShape(6.dp)
                    )
            ) {

                Text(
                    text = "Change Password",
                    textAlign = TextAlign.Center,
                    color = Color(0xff1e1e1e),
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier
                        .padding(start = 7.dp, top = 5.dp)
                        .clickable {
                            navController.navigate(Screen.ResetPassword.name)
                        }
                )


            }

            Text(
                text = "**********",
                color = Color(0xff1e1e1e),
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier
                    .padding(top = 5.dp)
                    .padding(start = 20.dp)
            )

        }

        Box(
            modifier = Modifier
                .padding(top = 245.dp)
                .padding(start = 10.dp)
                .requiredWidth(width = 120.dp)
                .requiredHeight(height = 50.dp)
                .clip(shape = RoundedCornerShape(23.59.dp))
                .background(color = Color(0xfff24822)))
        {

            Text(
                text = "LOGOUT",
                textAlign = TextAlign.Center,
                color = Color.White,
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier
                    .padding(start = 20.dp, top = 10.dp)
                    .clickable {
                        navController.navigate(Screen.Login.name)
                    }
            )

        }

    }

}