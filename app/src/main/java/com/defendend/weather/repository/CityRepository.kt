package com.defendend.weather.repository

import com.defendend.weather.database.CityDao
import com.defendend.weather.ui.weather_list.City
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

private const val DEFAULT_CITY = "default"

@Singleton
class CityRepository @Inject constructor(private val cityDao: CityDao) {

    suspend fun getCities(): List<City> {
        val cities = cityDao.getCities()
        val city = mutableListOf(cities.first { it.id == DEFAULT_CITY })
        city.addAll(cities.filter { it.id != DEFAULT_CITY })
        return city.toList()
    }

    suspend fun getCity(id: String): City? = cityDao.getCity(id = id)

    fun citiesFlow(): Flow<List<City>> = cityDao.citiesFlow()

    fun addCity(city: City) {
        cityDao.addCity(city = city)
    }

    fun updateCity(city: City) {
        cityDao.updateCity(city = city)
    }

    fun deleteCity(city: City) {
        cityDao.deleteCity(city = city)
    }
}