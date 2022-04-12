package com.defendend.weather.ui.base

import com.defendend.weather.api.WeatherApi
import com.defendend.weather.api.WeatherApi.Constants.API_KEY
import com.defendend.weather.features.city.CityNameResponse
import com.defendend.weather.features.weather.Daily
import com.defendend.weather.features.weather.Hourly
import com.defendend.weather.features.weather.WeatherResponseWrapper
import com.defendend.weather.location.LocationProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val locationProvider: LocationProvider,
    private val weatherApi: WeatherApi
) : BaseViewModel<WeatherState>() {

    private val scope = CoroutineScope(Dispatchers.IO)

    override fun createInitialState(): WeatherState = WeatherState.Loading

    init {
        safeIoLaunch {
            observeLocation()
        }
    }

    private fun observeLocation() = scope.launch {
        locationProvider.coordinates.collect {
            val (lat, lon) = it

            updateWeather(
                lat = lat,
                lon = lon
            )
        }
    }

    private suspend fun updateWeather(lat: Double, lon: Double) {
        val weather = getCurrentWeather(
            lat = lat,
            lon = lon
        )

        val cityName = getCurrentCityName(
            lat = lat,
            lon = lon
        )

        var state: WeatherState? = null
        weather.onSuccess { weatherResponseWrapper ->
            cityName.onSuccess { cityResponseWrapper ->
                state = createNewState(
                    weather = weatherResponseWrapper,
                    cityName = cityResponseWrapper
                )
            }
        }.onFailure {

        }
        state?.let { postState(it) }
    }

    private fun createNewState(
        weather: WeatherResponseWrapper,
        cityName: CityNameResponse
    ): WeatherState {

        val currentCity = cityName.firstOrNull()?.localNames?.ru
            ?: cityName.firstOrNull()?.name.orEmpty()

        val currentWeather = weather.current
        val currentDay = weather.daily?.firstOrNull()


        val currentTemperature = currentWeather?.temp?.roundToInt().toString()
        val description =
            weather.current?.weather?.firstOrNull()?.description.orEmpty().firstCharToUpperCase()


        val minTemp = currentDay?.temp?.min?.roundToInt() ?: 0
        val maxTemp = currentDay?.temp?.max?.roundToInt() ?: 0

        val hourly = weather.hourly.orEmpty()
        val daily = weather.daily.orEmpty()

        val tomorrowInfo = isTempTomorrowGrow(daily = daily)

        val uvIndex = currentWeather?.uvi?.roundToInt() ?: 0
        val uvIndexLevel = getUvIndexLevel(uvIndex = uvIndex)

        val uvIndexDescription = getUvIndexDescription(
            uvIndex = uvIndex,
            hourly = hourly
        )

        val sunrise = currentWeather?.sunrise?.times(1000) ?: 0
        val sunset = currentWeather?.sunset?.times(1000) ?: 0

        val windSpeed = currentDay?.windSpeed?.roundToInt() ?: 0
        val windGust = currentDay?.windGust?.roundToInt() ?: 0
        val windDirection = getWindDirection(currentDay?.windDeg ?: 0)

        return WeatherState.Data(
            currentCity = currentCity,
            currentTemperature = currentTemperature,
            description = description,
            minTemp = minTemp,
            maxTemp = maxTemp,
            tomorrowInfo = tomorrowInfo,
            hourly = hourly,
            daily = daily,
            uvIndex = "$uvIndex",
            uvIndexLevel = uvIndexLevel,
            uvIndexDescription = uvIndexDescription,
            sunrise = sunrise,
            sunset = sunset,
            windSpeed = windSpeed,
            windGust = windGust,
            windDirection = windDirection
        )
    }

    private fun getWindDirection(windDeg: Int): String {
        return when (windDeg*100) {
            in 0..11_25 -> "Северный"
            in 11_26..33_75 -> "Сервеный, северо-восточный"
            in 33_76..56_25 -> "Северо-восточный"
            in 56_26..78_75 -> "Восточный, северо-восточный"
            in 78_76..101_25 -> "Восточный"
            in 101_26..123_75 -> "Восточный, юго-восточный"
            in 123_76..146_25 -> "Юго-восточный"
            in 146_26..168_25 -> "Южный, юго-восточный"
            in 168_26..191_25 -> "Южный"
            in 191_26..213_75 -> "Южный, юго-западный"
            in 213_76..236_25 -> "Юго-западный"
            in 236_26..258_75 -> "Западный, юго-западный"
            in 258_76..281_25 -> "Западный"
            in 281_26..303_75 -> "Западный, северо-западный"
            in 303_76..326_25 -> "Северо-западный"
            in 326_26..348_75 -> "Северный, северо-западный"
            in 348_76..360_00 -> "Северный"
            else -> ""
        }
    }

    private fun getUvIndexDescription(uvIndex: Int, hourly: List<Hourly>): String {
        val levelUvi = getUvIndexLevel(uvIndex = uvIndex).lowercase()
        var maxUviInDay = uvIndex


        val dateFormat: DateFormat = SimpleDateFormat("H")
        dateFormat.timeZone = TimeZone.getDefault()

        var lastHourUvi = 0

        for (hour in hourly) {
            val utcTimeStamp = hour.dt ?: 0
            val uctLocalDateTime = Date(utcTimeStamp)

            val hourInDay = dateFormat.format(Date()).toInt()
            if (hourInDay == 0) {
                break
            }
            val uviInHour = hour.uvi?.roundToInt() ?: 0
            if (uviInHour > maxUviInDay) maxUviInDay = uviInHour
            lastHourUvi = uviInHour
        }
        return if (maxUviInDay > uvIndex) {
            val maxLevel = getUvIndexLevel(maxUviInDay).lowercase()
            if (maxLevel == levelUvi) {
                "Остается $levelUvi до конца дня"
            } else {
                "В течении дня станет: $maxLevel"
            }
        } else {
            val endDayLevel = getUvIndexLevel(lastHourUvi).lowercase()
            if (endDayLevel == levelUvi) {
                "Остается $levelUvi до конца дня"
            } else {
                "К концу дня уменшится до: $endDayLevel"
            }
        }
    }

    private fun String.firstCharToUpperCase(): String {
        if (this.isEmpty()) {
            return this
        }

        return this.replaceFirst(this[0], this[0].uppercaseChar())
    }

    private fun getUvIndexLevel(uvIndex: Int): String {
        return when (uvIndex) {
            in 0..2 -> "Низкий"
            in 3..5 -> "Умеренный"
            in 6..7 -> "Высокий"
            in 8..10 -> "Очень высокий"
            in 11..13 -> "Черезмерный"
            else -> {
                "Неопределен"
            }
        }
    }

    private fun isTempTomorrowGrow(daily: List<Daily>): Pair<Pair<Boolean, Boolean>, Int> {
        if (daily.isEmpty()) {
            return (false to false) to -1
        }
        val maxTemp = daily.firstOrNull()?.temp?.max?.roundToInt() ?: 0

        var maxTempTomorrow = 0
        if (daily.size > 1) {
            maxTempTomorrow = daily[1].temp?.max?.roundToInt() ?: 0
        }

        var isTempNotChanged = false

        val isTempGrowTomorrow =
            if (maxTempTomorrow > maxTemp) {
                true
            } else {
                if (maxTempTomorrow < maxTemp) {
                    false
                } else {
                    isTempNotChanged = true
                    false
                }
            }

        return (isTempNotChanged to isTempGrowTomorrow) to maxTempTomorrow
    }

    private suspend fun getCurrentCityName(lat: Double, lon: Double): Result<CityNameResponse> {

        return safeRunCatching {
            weatherApi.getCityNameFromLocation(
                latitude = lat.toString(),
                longitude = lon.toString()
            )
        }

    }

    private suspend fun getCurrentWeather(
        lat: Double,
        lon: Double
    ): Result<WeatherResponseWrapper> {

        return safeRunCatching {
            weatherApi.getWeatherFromGeolocation(
                latitude = lat.toString(),
                longitude = lon.toString(),
                API_KEY = API_KEY
            )
        }

    }


}