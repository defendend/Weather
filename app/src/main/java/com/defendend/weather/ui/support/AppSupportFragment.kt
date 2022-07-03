package com.defendend.weather.ui.support

import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.defendend.weather.R
import com.defendend.weather.databinding.AppSupportFragmentBinding

class AppSupportFragment : Fragment(R.layout.app_support_fragment) {

    private val binding: AppSupportFragmentBinding by viewBinding(
        AppSupportFragmentBinding::bind
    )


    companion object {
        const val TAG = "appSupport"
    }
}