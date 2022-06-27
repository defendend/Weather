package com.defendend.weather.models.city

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CityUi(
    val id: Int,
    val name: String,
    val lat: Double,
    val lon: Double,
    val timeZone: String
) : Parcelable