package com.example.aerosense_app.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.aerosense_app.HomeData

@Database(entities = [HomeData::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun homeDataDao(): HomeDataDao
}
