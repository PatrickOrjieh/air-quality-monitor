package com.example.aerosense_app.api

import com.example.aerosense_app.HomeData
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val confirmPassword: String,
    val modelNumber: String
)

data class RegisterResponse(
    val message: String,
    val firebaseUID: String,
    val userID: Int
)

interface ApiService {

    @GET("/api/home")
    fun getMeasures(): Single<List<HomeData>>

    @POST("/api/register")
    fun registerUser(@Body request: RegisterRequest): Single<RegisterResponse>

}
