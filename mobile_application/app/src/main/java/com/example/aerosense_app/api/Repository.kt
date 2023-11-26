package com.example.aerosense_app.api

import com.example.aerosense_app.HomeData
import com.example.aerosense_app.RegistrationData
import io.reactivex.Single

class Repository(private val apiService: ApiService) {
    fun getHomeData(): Single<List<HomeData>> {
        return apiService.getMeasures()
    }

    fun registerData(data: RegistrationData): Single<RegisterResponse> {
        val request = RegisterRequest(data.name, data.email, data.password, data.confirmPassword, data.modelNumber)
        return apiService.registerUser(request)
    }

}
