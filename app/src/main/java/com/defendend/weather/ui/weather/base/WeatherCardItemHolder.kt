package com.defendend.weather.ui.weather.base

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.defendend.weather.R
import com.defendend.weather.databinding.ListItemCurrentWeatherBinding
import com.defendend.weather.databinding.ListItemCurrentWeatherMoreInfoBinding
import com.defendend.weather.models.weather.TileItem

class WeatherCardItemHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val context = view.context
    private lateinit var cardItem: TileItem

    private val bindingCardItem: ListItemCurrentWeatherBinding by viewBinding(
        ListItemCurrentWeatherBinding::bind
    )

    private val bindingCardItemAdditional: ListItemCurrentWeatherMoreInfoBinding by viewBinding(
        ListItemCurrentWeatherMoreInfoBinding::bind
    )

    fun bind(cardItem: TileItem) {
        this.cardItem = cardItem


        when (cardItem) {
            is TileItem.BasicItem -> {
                bindingCardItem.apply {
                    titleTextView.text = context.getString(cardItem.title)
                    titleTextView.setCompoundDrawablesWithIntrinsicBounds(
                        cardItem.drawableStartCompat,
                        0,
                        0,
                        0
                    )
                    if (cardItem.valueResId != null) {
                        valueTextView.text = context.getString(cardItem.valueResId, cardItem.value)
                    } else {
                        valueTextView.text = cardItem.value
                    }
                    val descriptionResId = cardItem.descriptionResId
                    if (descriptionResId != null) {
                        if (cardItem.descriptionValue.isNotBlank()) {
                            primaryInfoTextView.text =
                                context.getString(descriptionResId, cardItem.descriptionValue)
                        } else {
                            primaryInfoTextView.text = context.getString(descriptionResId)
                        }

                    }
                }
            }
            is TileItem.UvIndex -> {
                bindingCardItemAdditional.apply {
                    titleTextView.text = context.getString(cardItem.title)
                    titleTextView.setCompoundDrawablesWithIntrinsicBounds(
                        cardItem.drawableStartCompat,
                        0,
                        0,
                        0
                    )
                    valueTextView.text = cardItem.value
                    primaryInfoTextView.text = context.getString(cardItem.uvIndexLevel)

                    val (descriptionRes, uviLevelRes) = cardItem.uvIndexDescription
                    val uviLevel = context.getString(uviLevelRes).lowercase()
                    val descriptionUvi = context.getString(descriptionRes, uviLevel)

                    descriptionTextView.text = descriptionUvi
                }

            }
            is TileItem.Wind -> {
                bindingCardItemAdditional.apply {
                    titleTextView.text = context.getString(cardItem.title)
                    titleTextView.setCompoundDrawablesWithIntrinsicBounds(
                        cardItem.drawableStartCompat,
                        0,
                        0,
                        0
                    )

                    valueTextView.text = context.getString(cardItem.valueResId, cardItem.value)
                    primaryInfoTextView.text = context.getString(cardItem.windDirection)
                    val windGust = context.getString(cardItem.windGustResId, cardItem.windGustValue)
                    descriptionTextView.text = context.getString(R.string.wind_gust, windGust)
                }
            }
        }
    }

}