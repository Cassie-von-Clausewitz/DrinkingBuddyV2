package com.kyleriedemann.drinkingbuddy.ui.notifications

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kyleriedemann.drinkingbuddy.data.models.Notification

@BindingAdapter("app:notifications")
fun setItems(listView: RecyclerView, items: List<Notification>) {
    (listView.adapter as NotificationsAdapter).submitList(items)
}