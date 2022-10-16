package com.defendend.weather.ui.settings

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.defendend.weather.R
import com.defendend.weather.databinding.SettingsFragmentBinding
import com.defendend.weather.models.city.CityUi
import com.defendend.weather.ui.base.UiEffect
import com.defendend.weather.ui.support.AppSupportFragment
import com.defendend.weather.ui.weather_list.City
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


private const val DIALOG_CITY = "dialogCityDelete"
private const val DIALOG_RESULT = "dialogResult"

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.settings_fragment) {

    private val binding: SettingsFragmentBinding by viewBinding(
        SettingsFragmentBinding::bind
    )
    private val viewModel by viewModels<SettingsViewModel>()
    private var adapterNames: CityNameSearchAdapter = CityNameSearchAdapter {
        viewModel.postEvent(SettingsEvent.OnCityClick(it))
    }
    private var adapterCityCard: CityCardAdapter = CityCardAdapter {
        viewModel.postEvent(SettingsEvent.OnCityCardClick(it))
    }

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

            val itemTouchHelperCallback =
                object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                    override fun onMove(
                        recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder
                    ): Boolean {
                        return true
                    }

                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        val position = viewHolder.bindingAdapterPosition
                        viewModel.postEvent(SettingsEvent.DeleteCityCard(position))
                    }

                    override fun getSwipeDirs(
                        recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder
                    ): Int {
                        return if (viewHolder.absoluteAdapterPosition == 0) {
                            0
                        } else {
                            super.getSwipeDirs(recyclerView, viewHolder)
                        }
                    }

                }
            val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
            itemTouchHelper.attachToRecyclerView(weatherCardRecycler)

            applicationSupport.setOnClickListener {
                showSupportTheApp()
            }

            backTextView.setOnClickListener {
                parentFragmentManager.popBackStack()
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.state.collect(::handleState)
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.effect.collect(::handleEffect)
            }
        }
    }

    private fun showSupportTheApp() {
        val fragment = AppSupportFragment()
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment, AppSupportFragment.TAG)
            .addToBackStack(AppSupportFragment.TAG)
            .commit()
    }

    private fun showDeleteAlert() {
        setFragmentResultListener(DIALOG_RESULT) { key, bundle ->
            val result = bundle.getInt(DIALOG_RESULT)
            if (result == 1) {
                viewModel.postEvent(SettingsEvent.CityDeleteConfirmed)
            }else {
                viewModel.postEvent(SettingsEvent.CancelDeleteCity)
            }

        }
        val dialog = CityDeleteDialog()
        dialog.show(parentFragmentManager, DIALOG_CITY)
    }

    private fun handleEffect(effect: UiEffect) {
        when (effect) {
            is SettingEffect.ClearSearchView -> onChooseCity()
            is SettingEffect.ConfirmCityDelete -> showDeleteAlert()
        }
    }

    private fun handleState(state: SettingsState) {
        when (state) {
            is SettingsState.Loading -> showLoading()
            is SettingsState.SearchCity -> showSearchCity(state = state)
            is SettingsState.Data -> showData(state)
            is SettingsState.ShowWeatherList -> onCardCityClick()
        }
    }

    private fun onChooseCity() {
        binding.apply {
            searchView.setQuery("", true)
            searchView.clearFocus()
        }
    }

    private fun onCardCityClick() {
        parentFragmentManager.popBackStack()
    }

    private fun updateSearchCities(names: List<CityUi>) {
        adapterNames.submitList(names)
    }

    private fun updateCitiesCards(cities: List<City>) {
        adapterCityCard.submitList(cities)
    }

    private fun showLoading() {
        updateSearchCities(emptyList())
        updateCitiesCards(emptyList())
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