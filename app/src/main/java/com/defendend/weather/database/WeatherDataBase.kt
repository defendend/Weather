package com.defendend.weather.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.defendend.weather.WeatherCity

@Database(entities = [WeatherCity::class], version = 1)
@TypeConverters(WeatherTypeConverters::class)
abstract class WeatherDataBase : RoomDatabase() {

    abstract fun weatherDao(): WeatherDao
}