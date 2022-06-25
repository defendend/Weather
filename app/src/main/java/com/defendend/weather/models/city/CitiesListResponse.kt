package com.defendend.weather.models.city

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CitiesListResponse(
    @SerialName("0")
    val cityOne: CityInfo? = null,
    @SerialName("1")
    val cityTwo: CityInfo? = null,
    @SerialName("2")
    val cityThree: CityInfo? = null,
    @SerialName("3")
    val cityFour: CityInfo? = null,
    @SerialName("4")
    val cityFive: CityInfo? = null,
    @SerialName("balans")
    val balans: Int? = null,
    @SerialName("limit")
    val limit: Int? = null
)