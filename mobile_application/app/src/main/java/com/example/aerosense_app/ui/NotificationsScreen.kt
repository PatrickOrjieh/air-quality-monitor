package com.example.aerosense_app.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.aerosense_app.FirebaseViewModel
import com.example.aerosense_app.R
import com.example.aerosense_app.api.Repository
import com.example.aerosense_app.ui.components.NavBar

@Composable
fun Notifications(navController: NavHostController, repository: Repository, firebaseModel: FirebaseViewModel){
    var time by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    NavBar(navController)


    // Sample notification data
    val notifications = listOf(
        Notification("16/12/2023 16:25", "Bad Air Quality Detected"),
        Notification("16/12/2023 16:25", "Bad Air Quality Detected"),
        Notification("16/12/2023 16:25", "Bad Air Quality Detected"),
        Notification("16/12/2023 16:25", "Bad Air Quality Detected"),
        Notification("16/12/2023 16:25", "Bad Air Quality Detected"),
        Notification("16/12/2023 16:25", "Bad Air Quality Detected"),
        Notification("16/12/2023 16:25", "Bad Air Quality Detected"),
        Notification("16/12/2023 16:25", "Bad Air Quality Detected"),
        Notification("16/12/2023 16:25", "Bad Air Quality Detected"),
        Notification("16/12/2023 16:25", "Bad Air Quality Detected"),
        Notification("16/12/2023 16:25", "Bad Air Quality Detected"),
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 70.dp),
    ) {

        item {
            Text(
                text = "Notifications",
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Medium),
                modifier = Modifier
                    .padding(top=10.dp, start=55.dp)
            )
        }

        items(notifications) { notification ->
            NotificationCard(notification = notification)
        }
    }
}

data class Notification(
    val sender: String,
    val message: String,
)

@Composable
fun NotificationCard(notification: Notification) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.alert),
                contentDescription = "alert bell",
                modifier = Modifier
                    .requiredSize(50.dp)
                    .padding(start=15.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = notification.sender,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                )

                Text(
                    text = notification.message,
                    fontSize = 14.sp,
                )
            }
        }
    }
}