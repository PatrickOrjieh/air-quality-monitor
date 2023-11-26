package com.example.aerosense_app.api

import com.example.aerosense_app.HomeData
import com.example.aerosense_app.LoginRequest
import com.example.aerosense_app.LoginResponse
import com.example.aerosense_app.RegisterRequest
import com.example.aerosense_app.RegisterResponse
import io.reactivex.Single
import retrofit2.Call

class Repository(private val apiService: ApiService) {
    fun getHomeData(): Single<List<HomeData>> {
        return apiService.getMeasures()
    }

    fun registerData(data: RegisterRequest): Single<RegisterResponse> {
        val request = RegisterRequest(data.name, data.email, data.password, data.confirmPassword, data.modelNumber)
        return apiService.registerUser(request)
    }

    fun loginUser(email: String, password: String): Call<LoginResponse> {
        val loginRequest = LoginRequest(email, password)
        return apiService.login(loginRequest)
    }

}
