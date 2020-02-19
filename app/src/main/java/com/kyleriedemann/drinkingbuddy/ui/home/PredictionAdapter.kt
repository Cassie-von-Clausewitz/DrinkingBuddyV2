package com.kyleriedemann.drinkingbuddy.ui.home

import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener

object PredictionAdapter {
    @BindingAdapter("prediction")
    @JvmStatic fun setPrediction(view: EditText, value: Float) {
        view.setText(value.toString())
    }

    @InverseBindingAdapter(attribute = "prediction")
    @JvmStatic fun getPrediction(view: EditText): Float {
        return view.text.toString().toFloat()
    }

    @BindingAdapter("predictionAttrChanged")
    @JvmStatic fun setListener(view: EditText, listener: InverseBindingListener?) {
        view.doAfterTextChanged {
            listener?.onChange()
        }
    }
}