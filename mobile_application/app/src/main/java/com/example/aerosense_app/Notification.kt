package com.example.aerosense_app

import java.time.LocalDateTime

data class Notification(
    val time: LocalDateTime,
    val header: String,
    val message: String
)