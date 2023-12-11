package com.example.aerosense_app

import android.app.Application
import androidx.room.Room
import com.example.aerosense_app.database.AppDatabase

class Aerosense : Application() {

    lateinit var db: AppDatabase

    override fun onCreate() {
        super.onCreate()
        db = Room.databaseBuilder(this, AppDatabase::class.java, "aerosense-db").build()
    }
}
