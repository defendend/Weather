package com.defendend.weather.di

import android.content.Context
import androidx.room.Room
import com.defendend.weather.database.CityDao
import com.defendend.weather.database.CityDataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val DATABASE_NAME = "weatherCityDataBase"

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): CityDataBase = Room.databaseBuilder(
        context,
        CityDataBase::class.java,
        DATABASE_NAME
    ).fallbackToDestructiveMigration()
        .build()

    @Provides
    @Singleton
    fun provideCityDao(cityDataBase: CityDataBase): CityDao = cityDataBase.cityDao()
}