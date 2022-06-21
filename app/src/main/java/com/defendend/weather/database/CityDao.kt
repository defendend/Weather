package com.defendend.weather.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.defendend.weather.ui.weather_list.City
import kotlinx.coroutines.flow.Flow

@Dao
interface CityDao {

    @Query("SELECT * FROM city")
    suspend fun getCities(): List<City>

    @Query("SELECT * FROM city")
    fun citiesFlow(): Flow<List<City>>

    @Query("SELECT * FROM city WHERE name=:name")
    suspend fun getCity(name: String): City?

    @Insert
    fun addCity(city: City)

    @Delete
    fun deleteCity(city: City)
}