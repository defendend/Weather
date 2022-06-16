package com.defendend.weather.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.defendend.weather.database.CityDataBase
import com.defendend.weather.ui.weather_list.City
import java.util.concurrent.Executors

private const val DATABASE_NAME = "weatherCityDataBase"

class CityRepository private constructor(context: Context) {

    private val dataBase: CityDataBase = Room.databaseBuilder(
        context.applicationContext,
        CityDataBase::class.java,
        DATABASE_NAME
    ).build()

    private val cityDao = dataBase.cityDao()
    private val executor = Executors.newSingleThreadExecutor()

    fun getCities(): LiveData<List<City>> = cityDao.getCities()
    fun getCity(name: String): LiveData<City?> = cityDao.getCity(name = name)

    fun addCity(city: City) {
        executor.execute {
            cityDao.addCity(city = city)
        }
    }

    fun deleteCity(city: City) {
        executor.execute {
            cityDao.deleteCity(city = city)
        }
    }

    companion object {
        private var INSTANCE: CityRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = CityRepository(context)
            }
        }

        fun get(): CityRepository {
            return INSTANCE ?: throw IllegalStateException("City repository must be initialized")
        }
    }
}