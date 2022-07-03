package com.defendend.weather.repository

import com.defendend.weather.api.CityApi
import com.defendend.weather.models.city.CitiesListResponse
import com.defendend.weather.models.city.CityInfo
import kotlinx.coroutines.CancellationException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CityNameRepository @Inject constructor(
    private val cityApi: CityApi
) {

    suspend fun getCitiesNames(cityName: String): Result<List<CityInfo>> {
        return getCitiesFromPartsOfName(cityName = cityName)
            .map {
                getCitiesNamesList(citiesListResponse = it)
            }
    }

    private fun getCitiesNamesList(citiesListResponse: CitiesListResponse): List<CityInfo> {

        val cityOne = citiesListResponse.cityOne
        val cityTwo = citiesListResponse.cityTwo
        val cityThree = citiesListResponse.cityThree
        val cityFour = citiesListResponse.cityFour
        val cityFive = citiesListResponse.cityFive

        val cities = listOf(
            cityOne,
            cityTwo,
            cityThree,
            cityFour,
            cityFive
        )


        return cities.mapNotNull { it }
    }

    private fun getCityNameString(fullName: String): String {
        return fullName //нужно будет реализовать логику обработки
    }

    private suspend fun getCitiesFromPartsOfName(
        cityName: String
    ): Result<CitiesListResponse> {

        return safeRunCatching {
            cityApi.getCityList(
                cityName = cityName
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