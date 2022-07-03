package com.defendend.weather.models.city

import com.defendend.weather.api.WeatherApi.Companion.TAG_RU
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class CityInfo(
//    @SerialName("area")
//    val area: Int? = null,
//    @SerialName("country")
//    val country: String? = null,
    @SerialName("english")
    val english: String? = null,
    @SerialName("full_english")
    val fullEnglish: String? = null,
    @SerialName("full_name")
    val fullName: String? = null,
//    @SerialName("geonameid")
//    val geonameid: Any? = null,
    @SerialName("id")
    val id: Int? = null,
//    @SerialName("iso")
//    val iso: String? = null,
    @SerialName("latitude")
    val latitude: Double? = null,
//    @SerialName("level")
//    val level: Int? = null,
    @SerialName("longitude")
    val longitude: Double? = null,
    @SerialName("name")
    val name: String? = null,
//    @SerialName("post")
//    val post: Int? = null,
//    @SerialName("rajon")
//    val rajon: Int? = null,
//    @SerialName("sound")
//    val sound: String? = null,
//    @SerialName("sub_rajon")
//    val sub_rajon: Int? = null,
//    @SerialName("telcod")
//    val telcod: Int? = null,
//    @SerialName("time_zone")
//    val timeZone: Int? = null,
    @SerialName("tz")
    val tz: String? = null
//    @SerialName("vid")
//    val vid: Int? = null,
//    @SerialName("wiki")
//    val wiki: String? = null
) {
    fun convertToUi(): CityUi {
        val localTag = Locale.getDefault().toLanguageTag().take(2)

        val name = if (localTag == TAG_RU) {
            fullName.orEmpty()
        } else {
            fullEnglish.orEmpty()
        }

        return CityUi(
            id = (id ?: 0).toString(),
            name = name,
            lat = latitude ?: 0.0,
            lon = longitude ?: 0.0,
            timeZone = tz.orEmpty()
        )
    }
}