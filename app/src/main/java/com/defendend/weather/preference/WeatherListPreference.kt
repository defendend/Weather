package com.defendend.weather.preference

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

private const val POSITION_FILE = "_position"
private const val POSITION = "position"

@Singleton
class WeatherListPreference @Inject constructor(@ApplicationContext context: Context) {
    private val preferences = context.getSharedPreferences(
        context.applicationContext.packageName + POSITION_FILE,
        Context.MODE_PRIVATE
    )
    private val _position = MutableStateFlow(getPosition())
    val position = _position.asStateFlow()

    fun setPosition(position: Int) {
        _position.value = position
        preferences.edit {
            putInt(POSITION, position)
        }
    }

    fun getPosition(): Int {
        return preferences.getInt(POSITION, 0)
    }
}
