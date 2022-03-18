package com.defendend.weather.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.defendend.weather.WeatherCity
import java.util.*

@Dao
interface WeatherDao {

    @Query("SELECT * FROM weathercity")
    fun getWeathers(): LiveData<List<WeatherCity>>

    @Query("SELECT * FROM weathercity WHERE name=(:id)")
    fun getWeather(id: UUID): LiveData<WeatherCity?>

    @Update
    fun updateWeatherCity(weatherCity: WeatherCity)

    @Insert
    fun addWeatherCity(weatherCity: WeatherCity)
}