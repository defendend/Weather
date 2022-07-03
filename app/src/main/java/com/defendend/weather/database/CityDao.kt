package com.defendend.weather.database

import androidx.room.*
import com.defendend.weather.ui.weather_list.City
import kotlinx.coroutines.flow.Flow

@Dao
interface CityDao {

    @Query("SELECT * FROM city")
    suspend fun getCities(): List<City>

    @Query("SELECT * FROM city")
    fun citiesFlow(): Flow<List<City>>

    @Query("SELECT * FROM city WHERE id=:id")
    suspend fun getCity(id: String): City?

    @Insert
    fun addCity(city: City)

    @Update
    fun updateCity(city: City)

    @Delete
    fun deleteCity(city: City)
}