package com.defendend.weather.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.defendend.weather.ui.weather_list.City

@Dao
interface CityDao {

    @Query("SELECT * FROM city")
    fun getCities(): LiveData<List<City>>

    @Query("SELECT * FROM city WHERE name=(:name)")
    fun getCity(name: String): LiveData<City?>

    @Insert
    fun addCity(city: City)

    @Delete
    fun deleteCity(city: City)
}