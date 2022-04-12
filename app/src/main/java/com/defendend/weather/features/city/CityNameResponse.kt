package com.defendend.weather.features.city

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

typealias CityNameResponse = List<CityNameResponseItem>

@Serializable
data class CityNameResponseItem(
    @SerialName("country")
    val country: String? = null,
    @SerialName("lat")
    val lat: Double ? = null,
    @SerialName("local_names")
    val localNames: LocalNames? = null,
    @SerialName("lon")
    val lon: Double ? = null,
    @SerialName("name")
    val name: String ? = null,
    @SerialName("state")
    val state: String? = null
)

@Serializable
data class LocalNames(
    @SerialName("en")
    val en: String? = null,
    @SerialName("ru")
    val ru: String? = null
)