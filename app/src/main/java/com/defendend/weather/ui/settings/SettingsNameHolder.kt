package com.defendend.weather.ui.settings

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.defendend.weather.databinding.SearchCityCardBinding
import com.defendend.weather.models.city.CityUi

class SettingsNameHolder(view: View) : RecyclerView.ViewHolder(view) {


    private val bindingNameHolder: SearchCityCardBinding by viewBinding(
        SearchCityCardBinding::bind
    )

    fun bind(name: CityUi, onClick: (Int) -> Unit) {
        bindingNameHolder.cityName.text = name.name
        bindingNameHolder.root.setOnClickListener {
            onClick(name.id)
        }
    }
}