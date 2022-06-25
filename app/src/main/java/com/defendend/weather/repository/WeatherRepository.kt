package com.defendend.weather.repository

import com.defendend.weather.R
import com.defendend.weather.api.WeatherApi
import com.defendend.weather.api.WeatherApi.Companion.TAG_RU
import com.defendend.weather.models.city.CityNameResponse
import com.defendend.weather.models.weather.*
import kotlinx.coroutines.CancellationException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

@Singleton
class WeatherRepository @Inject constructor(
    private val weatherApi: WeatherApi
) {
    private val localTag: String
        get() = Locale.getDefault().toLanguageTag().substring(0, 2)

    suspend fun getWeather(lat: Double, lon: Double): Result<LocationWeather> {
        return getCurrentWeatherFromLocation(lat = lat, lon = lon)
            .flatMap { weather ->
                getCityName(lat = lat, lon = lon)
                    .map { weather to it }
            }.map {
                val (weather, city) = it
                currentWeatherInLocation(
                    weather = weather,
                    cityName = city
                )
            }
    }

    private fun currentWeatherInLocation(
        weather: WeatherResponseWrapper,
        cityName: CityNameResponse
    ): LocationWeather {

        val firstCity = cityName.firstOrNull()
        val name = firstCity?.name.orEmpty()
        val currentCity = if (localTag == TAG_RU) {
            firstCity?.localNames?.ru ?: name
        } else {
            firstCity?.localNames?.en ?: name
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
        var precipitation = (snow + rain).toString()
        if (precipitation.length > 4) {
            precipitation = precipitation.substring(0, 4)
        }

        val uvIndexDescription = getUvIndexDescription(
            uvIndex = uvIndex,
            hourly = hourly
        )

        val dateFormat: DateFormat = SimpleDateFormat("H:mm")
        dateFormat.timeZone = TimeZone.getDefault()

        val sunrise = currentWeather?.sunrise?.times(1000) ?: 0
        val sunriseTime = dateFormat.format(Date(sunrise))
        val sunset = currentWeather?.sunset?.times(1000) ?: 0
        val sunsetTime = dateFormat.format(Date(sunset))

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
        var dewPoint = currentWeather?.dew_point.toString()

        if (dewPoint.length > 5) {
            dewPoint = dewPoint.substring(0, 5)
        }

        val visibilityDistance = currentWeather?.visibility?.div(1000) ?: 0

        val uvIndexCard = TileItem.UvIndex(
            R.drawable.ic_sun,
            R.string.uvi_text_title,
            uvIndex.toString(),
            uvIndexLevel,
            uvIndexDescription
        )

        val windCard = TileItem.Wind(
            R.drawable.ic_wind,
            R.string.wind_text_title,
            windSpeed,
            R.string.wind_speed,
            windDirection,
            R.string.wind_gust,
            R.string.wind_gust_value,
            windGust
        )

        val sunriseCard = TileItem.BasicItem(
            R.drawable.ic_sunrise,
            R.string.sunrise_text_title,
            sunriseTime,
            null,
            R.string.sunset_text,
            sunsetTime
        )

        val precipitationCard = TileItem.BasicItem(
            R.drawable.ic_precipitation,
            R.string.precipitation_text_title,
            precipitation,
            R.string.precipitation_mm,
            R.string.precipitation_last_day
        )

        val feelsLikeCard = TileItem.BasicItem(
            R.drawable.ic_thermostat,
            R.string.feels_like_title,
            feelsLike.toString(),
            R.string.temperature_feels_like,
            feelsLikeDescription
        )

        val pressureCard = TileItem.BasicItem(
            R.drawable.ic_pressure,
            R.string.pressure_title,
            pressureMm,
            R.string.pressure_mm
        )

        val humidityCard = TileItem.BasicItem(
            R.drawable.ic_humidity,
            R.string.humidity_title,
            humidity.toString(),
            R.string.humidity_text_card_view,
            R.string.dew_point_text,
            dewPoint
        )

        val visibilityCard = TileItem.BasicItem(
            R.drawable.ic_visibility,
            R.string.visibility_title,
            visibilityDistance.toString(),
            R.string.visibility_distance
        )


        val cardItems = mutableListOf<TileItem>()
        cardItems.add(uvIndexCard)
        cardItems.add(windCard)

        cardItems.add(sunriseCard)
        cardItems.add(precipitationCard)

        cardItems.add(feelsLikeCard)
        cardItems.add(pressureCard)

        cardItems.add(humidityCard)
        cardItems.add(visibilityCard)



        return LocationWeather(
            currentCity = currentCity,
            currentTemperature = currentTemperature.toString(),
            description = description,
            minTemp = minTemp,
            maxTemp = maxTemp,
            tomorrowInfo = tomorrowInfo,
            hourly = hourly,
            daily = daily,
            cardItems = cardItems.toList()
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

    private fun isTempTomorrowGrow(daily: List<Daily>): Triple<Boolean, Boolean, Int> {
        if (daily.isEmpty()) {
            return Triple(first = false, second = false, third = -1)
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

        return Triple(
            first = isTempNotChanged,
            second = isTempGrowTomorrow,
            third = maxTempTomorrow
        )
    }

    private suspend fun getCityName(lat: Double, lon: Double): Result<CityNameResponse> {

        return safeRunCatching {
            weatherApi.getCityNameFromLocation(
                latitude = lat.toString(),
                longitude = lon.toString()
            )
        }

    }

    private suspend fun getCurrentWeatherFromLocation(
        lat: Double,
        lon: Double
    ): Result<WeatherResponseWrapper> {
        val languageTag = if (localTag == WeatherApi.TAG_RU) {
            WeatherApi.TAG_RU
        } else {
            WeatherApi.TAG_EN
        }

        return safeRunCatching {
            weatherApi.getWeatherFromGeolocation(
                latitude = lat.toString(),
                longitude = lon.toString(),
                language = languageTag
            )
        }
    }


    private inline fun <T> safeRunCatching(runBlock: () -> T): Result<T> {
        return try {
            val result = runBlock()
            Result.success(result)
        } catch (ex: Exception) {
            if (ex is CancellationException) {
                throw ex
            }
            Result.failure(ex)
        }
    }

    private inline fun <T, V> Result<T>.flatMap(flatMapBody: (T) -> Result<V>): Result<V> {
        fold(
            onSuccess = { return flatMapBody(it) },
            onFailure = { return Result.failure(it) }
        )
    }
}