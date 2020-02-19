package com.kyleriedemann.drinkingbuddy.ui.dashboard

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kyleriedemann.drinkingbuddy.data.models.Reading

@BindingAdapter("app:readings")
fun setItems(listView: RecyclerView, items: List<Reading>?) {
    if (items == null || items.isEmpty()) return
    (listView.adapter as DashboardAdapter).submitList(items)
}