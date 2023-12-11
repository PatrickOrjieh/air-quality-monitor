package com.example.aerosense_app.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.aerosense_app.ui.components.NavBar

//Github Copilot used while writing this code

@Composable
fun History(navController: NavHostController){
    var airPercent by remember { mutableIntStateOf(0) }
    var highPM by remember { mutableIntStateOf(0) }
    var highVOC by remember { mutableIntStateOf(0) }

    //hardcoded values for now
    airPercent = 80
    highPM = 100
    highVOC = 50

    NavBar(navController)

    Text(
        text = "History",
        textAlign = TextAlign.Center,
        color = Color(0xff1e1e1e),
        style = TextStyle(
            fontSize = 35.sp,
            fontWeight = FontWeight.Medium),
        modifier = Modifier.padding(top = 80.dp)
    )

    Text(
        text = "Average Weekly Air Quality",
        textAlign = TextAlign.Center,
        color = Color(0xff1e1e1e),
        style = TextStyle(
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium),
        modifier = Modifier.padding(top = 140.dp)
    )
    
    DrawGraph()

    Box(
        modifier = Modifier.padding(top = 440.dp)
            .padding(start = 20.dp)
    ) {
        Text(
            text = "Average Air Cleanliness:",
            color = Color(0xff1e1e1e),
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            ),
        )

        Text(
            text = "-> $airPercent%",
            color = Color(0xff1e1e1e),
            style = TextStyle(
                fontSize = 20.sp,
            ),
            modifier = Modifier.padding(top = 30.dp)
        )
    }

    Box(
        modifier = Modifier.padding(top = 510.dp)
            .padding(start = 20.dp)
    ) {
        Text(
            text = "Highest PM This Week:",
            color = Color(0xff1e1e1e),
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            ),
        )

        Text(
            text = "-> $highPM µg/m^3",
            color = Color(0xff1e1e1e),
            style = TextStyle(
                fontSize = 20.sp,
            ),
            modifier = Modifier.padding(top = 30.dp)
        )

        Text(
            text = "-> Date: 11/12/23",
            color = Color(0xff1e1e1e),
            style = TextStyle(
                fontSize = 20.sp,
            ),
            modifier = Modifier.padding(top = 60.dp)
        )
    }

    Box(
        modifier = Modifier.padding(top = 610.dp)
            .padding(start = 20.dp)
    ) {
        Text(
            text = "Highest VOC This Week:",
            color = Color(0xff1e1e1e),
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            ),
        )

        Text(
            text = "-> $highVOC ppb",
            color = Color(0xff1e1e1e),
            style = TextStyle(
                fontSize = 20.sp,
            ),
            modifier = Modifier.padding(top = 30.dp)
        )

        Text(
            text = "-> Date: 10/12/23",
            color = Color(0xff1e1e1e),
            style = TextStyle(
                fontSize = 20.sp,
            ),
            modifier = Modifier.padding(top = 60.dp)
        )
    }

}

//Parts of graph code adapted from: https://stackoverflow.com/questions/58589507/draw-simple-xy-graph-plot-with-kotlin-without-3rd-party-library
@Composable
fun DrawGraph() {
    // Sample data for Monday to Sunday
    val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    // Sample data for percentages
    val percentages = listOf(50f, 80f, 30f, 60f, 90f, 20f, 70f)

    // Scaling factor for a larger graph
    val scaleFactor = 2.5f

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .padding(start= 30.dp)
            .requiredHeight(200.dp)
            .requiredWidth(250.dp)
            .offset(y = (-60).dp)
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        val maxPercentage = 100f
        val maxDayIndex = daysOfWeek.size - 1

        val scaleX = canvasWidth / (maxDayIndex * scaleFactor)
        val scaleY = canvasHeight / maxPercentage

        // Create Paint object
        val paint = Paint().asFrameworkPaint().apply {
            color = Color.Black.toArgb()
            textSize = 12.sp.toPx()
        }

        // Draw X-axis
        drawLine(
            color = Color.Black,
            start = Offset(0f, canvasHeight),
            end = Offset(canvasWidth, canvasHeight),
            strokeWidth = 2f
        )

        // Draw Y-axis
        drawLine(
            color = Color.Black,
            start = Offset(0f, 0f),
            end = Offset(0f, canvasHeight),
            strokeWidth = 2f
        )

        // Draw axis labels and align with data points
        daysOfWeek.forEachIndexed { index, day ->
            val xCoord = index * scaleX * scaleFactor
            val yCoord = canvasHeight - percentages[index] * scaleY

            // Draw X-axis label
            drawContext.canvas.nativeCanvas.drawText(
                day,
                xCoord,
                canvasHeight + 16.dp.toPx(),
                paint
            )
        }

        val percentageLabels = listOf("0%", "20%", "40%", "60%", "80%", "100%")
        percentageLabels.forEachIndexed { index, label ->
            val xCoord = -48.dp.toPx()
            val yCoord = canvasHeight - index * 20f * scaleY

            // Draw Y-axis label
            drawContext.canvas.nativeCanvas.drawText(
                label,
                xCoord,
                yCoord,
                paint
            )
        }

        // Draw the graph line
        val path = Path()
        daysOfWeek.forEachIndexed { index, _ ->
            val xCoord = index * scaleX * scaleFactor
            val yCoord = canvasHeight - percentages[index] * scaleY
            val point = Offset(xCoord, yCoord)

            if (index == 0) {
                path.moveTo(point.x, point.y)
            } else {
                path.lineTo(point.x, point.y)
            }
        }

        drawPath(path, color = Color.Blue, style = Stroke(4f))
    }
}


