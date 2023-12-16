package com.example.aerosense_app

import java.time.LocalDateTime

data class Notification(
    val time: Array<LocalDateTime>,
    val header: Array<String>,
    val message: Array<String>
)