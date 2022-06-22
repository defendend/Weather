package com.defendend.weather.ui.weather_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import by.kirich1409.viewbindingdelegate.viewBinding
import com.defendend.weather.R
import com.defendend.weather.databinding.FragmentWeatherListBinding
import com.defendend.weather.ui.settings.SettingsFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WeatherListFragment : Fragment() {

    private val binding by viewBinding(FragmentWeatherListBinding::bind)
    private val viewModel by viewModels<WeatherListViewModel>()
    private var adapterFragment: WeatherPagerAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_weather_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            adapterFragment = WeatherPagerAdapter(this@WeatherListFragment)
            viewPager.adapter = adapterFragment
            indicator.setViewPager(viewPager)
            settingButton.setOnClickListener { showSettings() }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.state.collect(::handleState)
            }
        }
    }

    private fun showSettings() {
        val fragment = SettingsFragment()
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment, SettingsFragment.TAG)
            .addToBackStack(SettingsFragment.TAG)
            .commit()
    }

    private fun handleState(state: WeatherListState) {
        when (state) {
            is WeatherListState.Loading -> showLoading()
            is WeatherListState.Data -> showData(state)
        }
    }

    private fun showLoading() {
        adapterFragment?.updateCities(emptyList())
    }

    private fun showData(state: WeatherListState.Data) {
        adapterFragment?.updateCities(state.cityNameList)
    }


    companion object {
        fun newInstance(): WeatherListFragment {
            return WeatherListFragment()
        }
    }
}