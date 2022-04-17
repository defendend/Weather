package com.defendend.weather.repository
//
//import android.content.Context
//import androidx.lifecycle.LiveData
//import androidx.room.Room
//import com.defendend.weather.models.city.WeatherCity
//import com.defendend.weather.database.WeatherDataBase
//import kotlinx.coroutines.flow.Flow
//import java.util.*
//import java.util.concurrent.Executors
//
//private const val DATABASE_NAME = "weather_database"
//
//class WeatherRepository private constructor(context: Context) {
//
//    private val dataBase: WeatherDataBase = Room.databaseBuilder(
//        context.applicationContext,
//        WeatherDataBase::class.java,
//        DATABASE_NAME
//    ).build()
//
//    private val weatherDao = dataBase.weatherDao()
//    private val executor = Executors.newSingleThreadExecutor()
//    private val fileDir = context.applicationContext.filesDir
//
//    fun getWeathers(): Flow<List<WeatherCity>> = weatherDao.getWeathers()
//    fun getWeather(id: UUID): Flow<WeatherCity?> = weatherDao.getWeather(id)
//
//    fun updateWeather(weatherCity: WeatherCity) {
//        executor.execute {
//            weatherDao.updateWeatherCity(weatherCity)
//        }
//    }
//
//    fun addWeatherCity(weatherCity: WeatherCity) {
//        executor.execute {
//            weatherDao.addWeatherCity(weatherCity)
//        }
//    }
//
//    companion object {
//        private var INSTANCE: WeatherRepository? = null
//
//        fun initialize(context: Context) {
//            if (INSTANCE == null){
//                INSTANCE = WeatherRepository(context)
//            }
//        }
//
//        fun get(): WeatherRepository {
//            return INSTANCE ?:throw IllegalStateException("Weather repository must be initialized")
//        }
//    }
//}