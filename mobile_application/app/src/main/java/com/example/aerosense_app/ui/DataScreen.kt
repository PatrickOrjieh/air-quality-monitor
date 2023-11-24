package com.example.aerosense_app.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.aerosense_app.R
import com.example.aerosense_app.Screen
import com.example.aerosense_app.ui.components.NavBar

@Composable
fun dataScreen(navController: NavHostController) {
    var percent by remember { mutableIntStateOf(0) }
    var time by remember { mutableStateOf("") }
    var pmTwo by remember { mutableIntStateOf(0) }
    var pmTen by remember { mutableIntStateOf(0) }
    var isMenuExpanded by remember { mutableStateOf(false) }

    percent = 58
    time = "12:56"
    pmTwo = 18
    pmTen = 42


    NavBar(navController)

    //Box for the circle
    Box(
        modifier = Modifier
            .requiredWidth(width = 320.dp)
            .requiredHeight(height = 291.dp)
            .offset(y = -180.dp)

    ) {
        // Use drawBehind to draw a hollow black circle with a border
        Box(
            modifier = Modifier
                .align(alignment = Alignment.TopStart)
                .offset(x = 15.dp, y = 19.dp)
                .requiredWidth(width = 287.dp)
                .requiredHeight(height = 272.dp)
                .drawBehind {
                    drawCircle(
                        color = Color.Black,
                        center = center,
                        radius = size.minDimension / 3,
                        style = Stroke(4.dp.toPx())
                    )
                }
        )
        Text(
            text = "57% Clean",
            color = Color(0xfff24822),
            lineHeight = 3.75.em,
            style = TextStyle(
                fontSize = 30.sp,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier
                .align(alignment = Alignment.Center)
        )
    }

    Box() {
        //Centered text underneath the circle box
        Text(
            text = "Updated: " + time,
            color = Color(0xff1e1e1e),
            lineHeight = 3.75.em,
            style = TextStyle(
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier
                .align(alignment = Alignment.Center)
                .offset(y = -50.dp)
        )
    }

    //Box for the particle stats
    Box(
        modifier = Modifier
            .requiredWidth(width = 534.dp)
            .requiredHeight(height = 123.dp)
            .offset(y = 40.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.particles),
            contentDescription = "download 1",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .align(alignment = Alignment.TopStart)
                .offset(
                    x = 100.dp,
                    y = 4.5.dp
                )
                .requiredWidth(width = 100.dp)
                .requiredHeight(height = 100.dp)
        )
        Text(
            text = "PM2.5 -",
            color = Color(0xff1e1e1e),
            lineHeight = 3.75.em,
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier
                .align(alignment = Alignment.TopStart)
                .offset(
                    x = 227.dp,
                    y = 15.dp
                )
        )
        Text(
            text = pmTwo.toString() + "µg/m^3",
            color = Color(0xffffa629),
            lineHeight = 3.75.em,
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier
                .align(alignment = Alignment.TopStart)
                .offset(
                    x = 320.dp,
                    y = 15.dp
                )
        )
        Text(
            text = "PM10 -",
            color = Color(0xff1e1e1e),
            lineHeight = 3.75.em,
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier
                .align(alignment = Alignment.TopStart)
                .offset(
                    x = 227.dp,
                    y = 63.dp
                )
        )
        Text(
            text = pmTen.toString() + "µg/m^3",
            color = Color(0xffffa629),
            lineHeight = 3.75.em,
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier
                .align(alignment = Alignment.TopStart)
                .offset(
                    x = 320.dp,
                    y = 63.dp
                )
        )
    }

    //Box for the gas stats
    Box(
        modifier = Modifier
            .requiredWidth(width = 541.dp)
            .requiredHeight(height = 137.dp)
            .offset(y = 180.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.gascloud),
            contentDescription = "download 2",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .requiredWidth(width = 100.dp)
                .requiredHeight(height = 100.dp)
                .align(alignment = Alignment.TopStart)
                .offset(
                    x = 105.dp,
                    y = 4.5.dp
                )
        )
        Text(
            text = "VOC Level -",
            color = Color(0xff1e1e1e),
            lineHeight = 3.75.em,
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier
                .align(alignment = Alignment.TopStart)
                .offset(
                    x = 227.dp,
                    y = 43.dp
                )
        )
        Text(
            text = "505 ppb",
            color = Color(0xfff24822),
            lineHeight = 3.75.em,
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier
                .align(alignment = Alignment.TopStart)
                .offset(
                    x = 345.dp,
                    y = 43.dp
                )
        )
    }

    //Box for location
    Box(
        modifier = Modifier
            .requiredWidth(width = 437.dp)
            .requiredHeight(height = 129.dp)
            .offset(y = 300.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.location),
            contentDescription = "download 3",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .requiredWidth(width = 100.dp)
                .requiredHeight(height = 100.dp)
                .align(alignment = Alignment.TopStart)
                .offset(
                    x = 50.dp,
                    y = 4.5.dp
                )
        )

        Box(modifier = Modifier.offset(
            x = 169.dp,
            y = 33.5.dp
        )
            .clickable {
            navController.navigate(Screen.Location.name)
        }) {

            Text(
                text = "View Location",
                color = Color(0xff237ec1),
                lineHeight = 3.75.em,
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .align(alignment = Alignment.TopStart)

            )
        }
    }

}