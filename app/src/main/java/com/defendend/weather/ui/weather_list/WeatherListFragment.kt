package com.defendend.weather.ui.weather_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.widget.ViewPager2
import by.kirich1409.viewbindingdelegate.viewBinding
import com.defendend.weather.R
import com.defendend.weather.databinding.FragmentWeatherListBinding
import com.defendend.weather.preference.WeatherListPreference
import com.defendend.weather.ui.base.UiEffect
import com.defendend.weather.ui.settings.SettingsFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class WeatherListFragment : Fragment() {

    private val binding by viewBinding(FragmentWeatherListBinding::bind)
    private val viewModel by viewModels<WeatherListViewModel>()
    private var adapterFragment: WeatherPagerAdapter? = null

    @Inject
    lateinit var weatherListPreference: WeatherListPreference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_weather_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapterFragment = WeatherPagerAdapter(this@WeatherListFragment)
        binding.apply {
            viewPager.adapter = adapterFragment
            adapterFragment
            indicator.setViewPager(viewPager)
            settingButton.setOnClickListener { showSettings() }
            adapterFragment?.registerAdapterDataObserver(indicator.adapterDataObserver)
            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    val event = WeatherListEvent.Position(position = position)
                    viewModel.postEvent(event = event)
                }
            })
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

    private fun showSettings() {
        setFragmentResultListener(SettingsFragment.TAG) { key, bundle ->
            val position = weatherListPreference.getPosition()
            val event = WeatherListEvent.Position(position = position)
            viewModel.postEvent(event = event)
        }
        val fragment = SettingsFragment()
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment, SettingsFragment.TAG)
            .addToBackStack(SettingsFragment.TAG)
            .commit()
    }

    private fun handleEffect(effect: UiEffect) {
        when (effect) {
//            is WeatherListEffect.UpdatePosition -> updateAdapterPosition(effect = effect)
            is WeatherListEffect.BadConnect -> {}
        }
    }

    private fun handleState(state: WeatherListState) {
        when (state) {
            is WeatherListState.Loading -> showLoading()
            is WeatherListState.Data -> showData(state)
        }
    }

    private fun updateAdapterPosition(effect: WeatherListEffect.UpdatePosition) {
        binding.viewPager.setCurrentItem(effect.position,false)
    }

    private fun showLoading() {
        adapterFragment?.updateCities(emptyList())
    }

    private fun showData(state: WeatherListState.Data) {
        adapterFragment?.updateCities(state.citiesUi)
        binding.viewPager.setCurrentItem(state.selectedPosition,false)
    }

    companion object {
        fun newInstance(): WeatherListFragment {
            return WeatherListFragment()
        }
    }
}