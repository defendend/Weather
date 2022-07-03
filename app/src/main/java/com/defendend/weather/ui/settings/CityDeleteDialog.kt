package com.defendend.weather.ui.settings

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.defendend.weather.R

private const val DIALOG_RESULT = "dialogResult"

class CityDeleteDialog : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.alert_dialog_card, null,false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog?.window?.setLayout(layoutParams.width, layoutParams.height)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val deleteButton = view.findViewById<TextView>(R.id.remove_button)
        val cancelButton = view.findViewById<TextView>(R.id.cancel_button)

        deleteButton.setOnClickListener {
            val result = 1
            setFragmentResult(DIALOG_RESULT, bundleOf(DIALOG_RESULT to result))
            dismiss()
        }

        cancelButton.setOnClickListener {
            val result = 0
            setFragmentResult(DIALOG_RESULT, bundleOf(DIALOG_RESULT to result))
            dismiss()
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        val result = 0
        setFragmentResult(DIALOG_RESULT, bundleOf(DIALOG_RESULT to result))
    }

}