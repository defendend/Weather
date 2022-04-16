package com.defendend.weather.ui.base

import android.provider.Settings.System.getConfiguration
import com.defendend.weather.api.WeatherApi
import com.defendend.weather.api.WeatherApi.Constants.API_KEY
import com.defendend.weather.api.WeatherApi.Constants.TAG_EN
import com.defendend.weather.api.WeatherApi.Constants.TAG_RU
import com.defendend.weather.features.city.CityNameResponse
import com.defendend.weather.features.weather.Daily
import com.defendend.weather.features.weather.Hourly
import com.defendend.weather.features.weather.WeatherResponseWrapper
import com.defendend.weather.location.LocationProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

        val locale = Locale.getDefault().toLanguageTag()

        val currentCity = when (locale.substring(0, 2)) {
            "ru" -> {
                cityName.firstOrNull()?.localNames?.ru
                    ?: cityName.firstOrNull()?.name.orEmpty()
            }
            "en" -> {
                cityName.firstOrNull()?.localNames?.en
                    ?: cityName.firstOrNull()?.name.orEmpty()
            }
            else -> {
                ""
            }
        }
        cityName.firstOrNull()?.localNames?.ru
            ?: cityName.firstOrNull()?.name.orEmpty()

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

    private fun getFeelsDescription(feelsLike: Int, currentTemperature: Int): String {
        val difference = feelsLike - currentTemperature

        when (Locale.getDefault().toLanguageTag().substring(0, 2)) {
            TAG_RU ->
                return if (difference < 0) {
                    when (difference) {
                        -1 -> "По ощущениям прохладнее из-за ветра."
                        else -> "По ощущениям холоднее из-за ветра."
                    }
                } else if (difference > 0) {
                    "По ощущениям жарче из-за отсутствия ветра."
                } else {
                    "По ощущениям примерно так же."
                }
            else ->
                return if (difference < 0) {
                    when (difference) {
                        -1 -> "Feels cooler because of the wind."
                        else -> "It feels colder because of the wind."
                    }
                } else if (difference > 0) {
                    "It feels hotter because of the lack of wind."
                } else {
                    "It feels about the same."
                }
        }


    }


    private fun getWindDirection(windDeg: Int): String {

        when (Locale.getDefault().toLanguageTag().substring(0, 2)) {
            TAG_RU ->
                return when (windDeg) {
                    in 0..11 -> "Северный"
                    in 12..33 -> "Северный, северо-восточный"
                    in 34..56 -> "Северо-восточный"
                    in 57..78 -> "Восточный, северо-восточный"
                    in 79..101 -> "Восточный"
                    in 102..123 -> "Восточный, юго-восточный"
                    in 124..146 -> "Юго-восточный"
                    in 147..168 -> "Южный, юго-восточный"
                    in 169..191 -> "Южный"
                    in 192..213 -> "Южный, юго-западный"
                    in 214..236 -> "Юго-западный"
                    in 237..258 -> "Западный, юго-западный"
                    in 259..281 -> "Западный"
                    in 282..303 -> "Западный, северо-западный"
                    in 304..326 -> "Северо-западный"
                    in 327..348 -> "Северный, северо-западный"
                    in 349..360 -> "Северный"
                    else -> ""
                }
            else ->
                return when (windDeg) {
                    in 0..11 -> "North"
                    in 12..33 -> "North, Northeast"
                    in 34..56 -> "Northeast"
                    in 57..78 -> "East, Northeast"
                    in 79..101 -> "Eastern"
                    in 102..123 -> "Eastern, Southeastern"
                    in 124..146 -> "Southeastern"
                    in 147..168 -> "South, South-east"
                    in 169..191 -> "South"
                    in 192..213 -> "South, Southwest"
                    in 214..236 -> "Southwest"
                    in 237..258 -> "West, Southwest"
                    in 259..281 -> "West"
                    in 282..303 -> "Western, North-western"
                    in 304..326 -> "Northwest"
                    in 327..348 -> "North, North-west"
                    in 349..360 -> "North"
                    else -> ""
                }

        }
    }

    private fun getUvIndexDescription(uvIndex: Int, hourly: List<Hourly>): String {
        val levelUvi = getUvIndexLevel(uvIndex = uvIndex).lowercase()
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
        when (Locale.getDefault().toLanguageTag().substring(0, 2)) {
            TAG_RU ->
                return if (maxUviInDay > uvIndex) {
                    val maxLevel = getUvIndexLevel(maxUviInDay).lowercase()
                    if (maxLevel == levelUvi) {
                        "Остается $levelUvi до конца дня."
                    } else {
                        "В течении дня станет: $maxLevel."
                    }
                } else {
                    val endDayLevel = getUvIndexLevel(lastHourUvi).lowercase()
                    if (endDayLevel == levelUvi) {
                        "Остается $levelUvi до конца дня."
                    } else {
                        "К концу дня уменшится до: $endDayLevel."
                    }
                }
            else ->
                return if (maxUviInDay > uvIndex) {
                    val maxLevel = getUvIndexLevel(maxUviInDay).lowercase()
                    if (maxLevel == levelUvi) {
                        "Stay $levelUvi for the rest of the day."
                    } else {
                        "During the day will become: $maxLevel."
                    }
                } else {
                    val endDayLevel = getUvIndexLevel(lastHourUvi).lowercase()
                    if (endDayLevel == levelUvi) {
                        "Stay $levelUvi for the rest of the day."
                    } else {
                        "By the end of the day will decrease to: $endDayLevel."
                    }
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
        return when (Locale.getDefault().toLanguageTag().substring(0, 2)) {
            TAG_RU ->
                when (uvIndex) {
                    in 0..2 -> "Низкий"
                    in 3..5 -> "Умеренный"
                    in 6..7 -> "Высокий"
                    in 8..10 -> "Очень высокий"
                    in 11..13 -> "Черезмерный"
                    else -> {
                        "Неопределен"
                    }
                }
            else ->
                when (uvIndex) {
                    in 0..2 -> "Low"
                    in 3..5 -> "Moderate"
                    in 6..7 -> "High"
                    in 8..10 -> "Very high"
                    in 11..13 -> "Excessive"
                    else -> {
                        "Not determined"
                    }
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

        return when (Locale.getDefault().toLanguageTag().substring(0, 2)) {
            TAG_RU ->
                safeRunCatching {
                    weatherApi.getWeatherFromGeolocation(
                        latitude = lat.toString(),
                        longitude = lon.toString(),
                        API_KEY = API_KEY,
                        language = TAG_RU
                    )
                }
            else ->
                safeRunCatching {
                    weatherApi.getWeatherFromGeolocation(
                        latitude = lat.toString(),
                        longitude = lon.toString(),
                        API_KEY = API_KEY,
                        language = TAG_EN
                    )
                }
        }
    }


}