package com.defendend.weather.ui.weather

import com.defendend.weather.R
import com.defendend.weather.api.WeatherApi
import com.defendend.weather.api.WeatherApi.Constants.API_KEY
import com.defendend.weather.api.WeatherApi.Constants.TAG_EN
import com.defendend.weather.api.WeatherApi.Constants.TAG_RU
import com.defendend.weather.location.LocationProvider
import com.defendend.weather.models.city.CityNameResponse
import com.defendend.weather.models.weather.Daily
import com.defendend.weather.models.weather.Hourly
import com.defendend.weather.models.weather.WeatherResponseWrapper
import com.defendend.weather.ui.base.BaseViewModel
import com.defendend.weather.ui.base.WeatherState
import dagger.hilt.android.lifecycle.HiltViewModel
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

    private val localTag: String
        get() = Locale.getDefault().toLanguageTag().substring(0, 2)

    override fun createInitialState(): WeatherState = WeatherState.Loading

    init {
        safeIoLaunch {
            observeLocation()
        }
    }

    private fun observeLocation() = safeIoLaunch {
        locationProvider.coordinates.collect {
            val (lat, lon) = it

            updateWeather(
                lat = lat,
                lon = lon
            )
        }
    }

    private suspend fun updateWeather(lat: Double, lon: Double) {
        val weather = getCurrentWeather(lat = lat, lon = lon)
            .flatMap { weather ->
                getCurrentCityName(lat = lat, lon = lon)
                    .map { weather to it }
            }.onSuccess {
                val (weather, city) = it
                val state = createNewState(
                    weather = weather,
                    cityName = city
                )
                postState(state)
            }.onFailure {

            }


    }

    private fun createNewState(
        weather: WeatherResponseWrapper,
        cityName: CityNameResponse
    ): WeatherState {

        val firstCity = cityName.firstOrNull()
        val name = firstCity?.name.orEmpty()

        val currentCity = when (localTag) {
            "ru" -> {
                firstCity?.localNames?.ru ?: name
            }
            "en" -> {
                firstCity?.localNames?.en ?: name
            }
            else -> {
                firstCity?.localNames?.en ?: name
            }
        }

        val currentWeather = weather.current
        val currentDay = weather.daily?.firstOrNull()

        val currentTemperature = currentWeather?.temp?.roundToInt() ?: 0
        val description =
            weather.current?.weather?.firstOrNull()?.description.orEmpty().firstCharToUpperCase()


        val minTemp = currentDay?.temp?.min?.roundToInt() ?: 0
        val maxTemp = currentDay?.temp?.max?.roundToInt() ?: 0

        val hourly = weather.hourly.orEmpty()
        val daily = weather.daily.orEmpty()

        val tomorrowInfo = isTempTomorrowGrow(daily = daily)

        val uvIndex = currentWeather?.uvi?.roundToInt() ?: 0
        val uvIndexLevel = getUvIndexLevel(uvIndex = uvIndex)

        val snow = currentDay?.snow ?: 0.0
        val rain = currentDay?.rain ?: 0.0
        val precipitation = (snow + rain).toString()

        val uvIndexDescription = getUvIndexDescription(
            uvIndex = uvIndex,
            hourly = hourly
        )

        val sunrise = currentWeather?.sunrise?.times(1000) ?: 0
        val sunset = currentWeather?.sunset?.times(1000) ?: 0

        val windSpeed = currentWeather?.windSpeed?.roundToInt() ?: 0
        val windGust = currentDay?.windGust?.roundToInt() ?: 0
        val windDirection = getWindDirection(currentWeather?.windDeg ?: 0)

        val feelsLike = currentWeather?.feelsLike?.roundToInt() ?: 0


        val feelsLikeDescription = getFeelsDescription(
            feelsLike = feelsLike,
            currentTemperature = currentTemperature
        )

        val pressure = currentWeather?.pressure ?: 0
        val pressureMm = (1.0 * pressure * 0.750064).roundToInt().toString()

        val humidity = currentWeather?.humidity ?: 0
        val dewPoint = currentWeather?.dew_point ?: 0.0

        val visibilityDistance = currentWeather?.visibility?.div(1000) ?: 0

        return WeatherState.Data(
            currentCity = currentCity,
            currentTemperature = currentTemperature.toString(),
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
            windDirection = windDirection,
            precipitation = precipitation,
            feelsLike = feelsLike.toString(),
            feelsLikeDescription = feelsLikeDescription,
            pressureMm = pressureMm,
            humidity = humidity,
            dewPoint = dewPoint.toString(),
            visibility = visibilityDistance
        )
    }

    private fun getFeelsDescription(feelsLike: Int, currentTemperature: Int): Int {
        val difference = feelsLike - currentTemperature

        return if (difference < 0) {
            when (difference) {
                -1 -> R.string.feels_cooler
                else -> R.string.feels_cold
            }
        } else if (difference > 0) {
            R.string.feels_hotter
        } else {
            R.string.feels_same
        }


    }


    private fun getWindDirection(windDeg: Int): Int {
        return when (windDeg) {
            in 0..11 -> R.string.north
            in 12..33 -> R.string.north_northeast
            in 34..56 -> R.string.northeast
            in 57..78 -> R.string.east_northeast
            in 79..101 -> R.string.eastern
            in 102..123 -> R.string.eastern_southeastern
            in 124..146 -> R.string.southeastern
            in 147..168 -> R.string.south_south_east
            in 169..191 -> R.string.south
            in 192..213 -> R.string.south_southwest
            in 214..236 -> R.string.southwest
            in 237..258 -> R.string.west_southwest
            in 259..281 -> R.string.west
            in 282..303 -> R.string.western_north_western
            in 304..326 -> R.string.northwest
            in 327..348 -> R.string.north_north_west
            in 349..360 -> R.string.north
            else -> R.string.empty_string
        }
    }

    private fun getUvIndexDescription(uvIndex: Int, hourly: List<Hourly>): Pair<Int, Int> {
        val levelUvi = getUvIndexLevel(uvIndex = uvIndex)
        var maxUviInDay = uvIndex


        val dateFormat: DateFormat = SimpleDateFormat("H")
        dateFormat.timeZone = TimeZone.getDefault()

        var lastHourUvi = 0

        for (hour in hourly) {

            val hourInDay = dateFormat.format(Date()).toInt()
            if (hourInDay == 0) {
                break
            }
            val uviInHour = hour.uvi?.roundToInt() ?: 0
            if (uviInHour > maxUviInDay) maxUviInDay = uviInHour
            lastHourUvi = uviInHour
        }

        return if (maxUviInDay > uvIndex) {
            val maxLevel = getUvIndexLevel(maxUviInDay)
            if (maxLevel == levelUvi) {
                (R.string.not_changed_lvl to levelUvi)
            } else {
                (R.string.during_in_day to maxLevel)
            }
        } else {
            val endDayLevel = getUvIndexLevel(lastHourUvi)
            if (endDayLevel == levelUvi) {
                (R.string.not_changed_lvl to levelUvi)
            } else {
                (R.string.decrease_in_day to endDayLevel)
            }
        }


    }

    private fun String.firstCharToUpperCase(): String {
        if (this.isEmpty()) {
            return this
        }

        return this.replaceFirst(this[0], this[0].uppercaseChar())
    }

    private fun getUvIndexLevel(uvIndex: Int): Int {
        return when (uvIndex) {
            in 0..2 -> R.string.low
            in 3..5 -> R.string.moderate
            in 6..7 -> R.string.high
            in 8..10 -> R.string.very_high
            in 11..13 -> R.string.excessive
            else -> {
                R.string.not_determined
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
        val languageTag = if (localTag == TAG_RU) {
            TAG_RU
        } else {
            TAG_EN
        }

        return safeRunCatching {
            weatherApi.getWeatherFromGeolocation(
                latitude = lat.toString(),
                longitude = lon.toString(),
                API_KEY = API_KEY,
                language = languageTag
            )
        }
    }


}