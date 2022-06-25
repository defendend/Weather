package com.defendend.weather.repository

import com.defendend.weather.database.CityDao
import com.defendend.weather.ui.weather_list.City
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton



@Singleton
class CityRepository @Inject constructor(private val cityDao: CityDao) {

    suspend fun getCities(): List<City> = cityDao.getCities()
    suspend fun getCity(name: String): City? = cityDao.getCity(name = name)

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