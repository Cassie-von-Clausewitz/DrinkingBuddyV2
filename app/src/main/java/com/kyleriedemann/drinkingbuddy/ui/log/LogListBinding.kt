package com.kyleriedemann.drinkingbuddy.ui.log

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kyleriedemann.drinkingbuddy.data.models.Log

@BindingAdapter("app:logs")
fun setItems(listView: RecyclerView, items: List<Log>?) {
    if (items == null || items.isEmpty()) return
    (listView.adapter as LogAdapter).submitList(items)
}