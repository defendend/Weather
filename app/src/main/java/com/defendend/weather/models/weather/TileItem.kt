package com.defendend.weather.models.weather

sealed class TileItem {
    data class BasicItem(
        val drawableStartCompat: Int,
        val title: Int,
        val value: String,
        val valueResId: Int? = null,
        val descriptionResId: Int? = null,
        val descriptionValue: String = ""
    ) : TileItem()

    data class UvIndex(
        val drawableStartCompat: Int,
        val title: Int,
        val value: String,
        val uvIndexLevel: Int,
        val uvIndexDescription: Pair<Int, Int>
    ) : TileItem()

    data class Wind(
        val drawableStartCompat: Int,
        val title: Int,
        val value: Int,
        val valueResId: Int,
        val windDirection: Int,
        val windGust: Int,
        val windGustResId: Int,
        val windGustValue: Int
    ) : TileItem()
}