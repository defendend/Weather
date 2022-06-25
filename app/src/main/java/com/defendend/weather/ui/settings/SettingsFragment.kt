package com.defendend.weather.ui.settings

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.defendend.weather.R
import com.defendend.weather.databinding.SettingsFragmentBinding
import com.defendend.weather.models.city.CityUi
import com.defendend.weather.ui.weather_list.City
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.settings_fragment) {

    private val binding: SettingsFragmentBinding by viewBinding(
        SettingsFragmentBinding::bind
    )
    private val viewModel by viewModels<SettingsViewModel>()
    private var adapterNames: CityNameSearchAdapter = CityNameSearchAdapter {
        viewModel.postEvent(SettingsEvent.OnCityClick(it))
    }
    private var adapterCityCard: CityCardAdapter = CityCardAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            chooseCityRecycler.layoutManager = LinearLayoutManager(context)
            chooseCityRecycler.adapter = adapterNames

            weatherCardRecycler.layoutManager = LinearLayoutManager(context)
            weatherCardRecycler.adapter = adapterCityCard

            searchView.apply {
                setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(cityName: String): Boolean {
                        return false
                    }

                    override fun onQueryTextChange(cityName: String): Boolean {
                        viewModel.postEvent(SettingsEvent.Query(cityName = cityName))
                        return true
                    }

                })
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.state.collect(::handleState)
            }
        }
    }

    private fun handleState(state: SettingsState) {
        when (state) {
            is SettingsState.Loading -> {}
            is SettingsState.SearchCity -> showSearchCity(state = state)
            is SettingsState.Data -> {
                showData(state)
            }
        }
    }

    private fun updateSearchCities(names: List<CityUi>) {
        adapterNames.submitList(names)
    }

    private fun updateCitiesCards(cities: List<City>) {
        adapterCityCard.submitList(cities)
    }

    private fun showSearchCity(state: SettingsState.SearchCity) {
        updateSearchCities(state.cityNames)
        updateCitiesCards(emptyList())
    }

    private fun showData(state: SettingsState.Data) {
        updateSearchCities(emptyList())
        updateCitiesCards(state.cities)
    }


    companion object {
        const val TAG = "settings"
    }
}