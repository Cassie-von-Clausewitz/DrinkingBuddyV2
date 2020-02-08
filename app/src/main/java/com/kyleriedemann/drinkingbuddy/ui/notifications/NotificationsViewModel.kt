package com.kyleriedemann.drinkingbuddy.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.kyleriedemann.drinkingbuddy.data.models.Notification

class NotificationsViewModel : ViewModel() {
    private val _items = MutableLiveData<List<Notification>>(emptyList())
    val items: LiveData<List<Notification>> = _items

    private val _loading = MutableLiveData<Boolean>(false)
    val loading: LiveData<Boolean> = _loading

    private val _errors = MutableLiveData<Exception>()
    val errors: LiveData<Exception> = _errors

    val empty: LiveData<Boolean> = Transformations.map(_items) {
        it.isEmpty()
    }

    fun loadNotifications() {
        TODO("Setup dagger for viewModel")
    }

    fun markNotificationRead(notification: Notification) {
        TODO("Setup dagger for viewModel")
    }

    fun clearError() = _errors.postValue(null)

    fun refresh() = loadNotifications()
}
