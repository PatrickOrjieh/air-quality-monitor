package com.example.aerosense_app
data class RegisterRequest(
        val name: String,
        val email: String,
        val password: String,
        val confirmPassword: String,
        val modelNumber: String
)
