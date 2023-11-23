package com.example.aerosense_app.ui

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.aerosense_app.R
import com.example.aerosense_app.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Register(navController: NavHostController) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var modelNum by remember { mutableStateOf("") }



    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.padding(start = 16.dp) // Add padding to the outer column
        ) {

            Spacer(modifier = Modifier.height(25.dp))

            // Login-related elements
            Text(
                text = "Create Account",
                style = TextStyle(
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .padding(start = 16.dp)
                    .align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(16.dp))

            //Name row with user image and text field
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.user),
                    contentDescription = "User Icon",
                    modifier = Modifier.size(35.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                TextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f), // Take remaining space in the Row
                    label = { Text("Full Name") }
                )
            }

            // Email Row with image and TextField
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.email),
                    contentDescription = "Email Icon",
                    modifier = Modifier.size(35.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                TextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f), // Take remaining space in the Row
                    label = { Text("Email") }
                )
            }

            // Password Row with image and TextField
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.password),
                    contentDescription = "Password Icon",
                    modifier = Modifier.size(35.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                TextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f), // Take remaining space in the Row
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password)
                )
            }

            //Confirm password row with image and text field
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.password),
                    contentDescription = "Password Icon",
                    modifier = Modifier.size(35.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                TextField(
                    value = confirm,
                    onValueChange = { confirm = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f), // Take remaining space in the Row
                    label = { Text("Confirm Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.modelnum),
                    contentDescription = "Model Number",
                    modifier = Modifier.size(35.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                TextField(
                    value = modelNum,
                    onValueChange = { modelNum = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f), // Take remaining space in the Row
                    label = { Text("Hub Model Number") },
                    visualTransformation = PasswordVisualTransformation(),
                )
            }

            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.padding(start = 230.dp)
            ) {
                Button(
                    onClick = {
                        navController.navigate(Screen.DataScreen.name)
                    },
                    modifier = Modifier
                        .height(53.dp),
                    shape = MaterialTheme.shapes.large // Apply rounded corners
                ) {
                    Text(text = "Register")
                }
            }

            Spacer(modifier = Modifier.height(60.dp))

            //Make text for not having account and click to register
            Text(
                text = "Already have an account?",
                style = TextStyle(
                    color = Color.Blue,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .padding(start = 16.dp)
                    .align(Alignment.Start)
                    .clickable {
                        navController.navigate(Screen.Login.name)
                    }
            )

        }
    }


}