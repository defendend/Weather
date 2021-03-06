package com.defendend.weather.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.defendend.weather.ui.weather_list.City

@Database(entities = [City::class], version = 6)
abstract class CityDataBase : RoomDatabase() {

    abstract fun cityDao(): CityDao
}