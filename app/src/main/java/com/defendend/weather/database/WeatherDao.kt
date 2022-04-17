package com.defendend.weather.database

//import androidx.room.Dao
//import androidx.room.Insert
//import androidx.room.Query
//import androidx.room.Update
//import com.defendend.weather.models.city.WeatherCity
//import kotlinx.coroutines.flow.Flow
//import java.util.*

//@Dao
//interface WeatherDao {

//    @Query("SELECT * FROM weathercity")
//    fun getWeathers(): Flow<List<WeatherCity>>
//
//    @Query("SELECT * FROM weathercity WHERE name=(:id)")
//    fun getWeather(id: UUID): Flow<WeatherCity?>
//
//    @Update
//    fun updateWeatherCity(weatherCity: WeatherCity)
//
//    @Insert
//    fun addWeatherCity(weatherCity: WeatherCity)
//}