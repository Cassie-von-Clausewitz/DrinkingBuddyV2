package com.kyleriedemann.drinkingbuddy.ui.log

import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.google.android.material.chip.Chip
import com.kyleriedemann.drinkingbuddy.data.models.LogLevel
import com.kyleriedemann.drinkingbuddy.ui.common.logLevelToColor

@BindingAdapter("app:level")
fun setLevel(chip: Chip, level: LogLevel) {
    val color = ContextCompat.getColor(chip.context, logLevelToColor(level))
    chip.chipBackgroundColor = ColorStateList.valueOf(color)
    chip.text = level.name
}