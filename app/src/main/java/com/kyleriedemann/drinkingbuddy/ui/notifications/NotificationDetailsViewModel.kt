package com.kyleriedemann.drinkingbuddy.ui.notifications

import androidx.lifecycle.*
import com.kyleriedemann.drinkingbuddy.data.LceState
import com.kyleriedemann.drinkingbuddy.data.models.Notification
import com.kyleriedemann.drinkingbuddy.data.source.NotificationRepository
import com.kyleriedemann.drinkingbuddy.di.ViewModelAssistedFactory
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.launch

const val NOTIFICATION_DETAILS_KEY = "NOTIFICATION_DETAILS_KEY"

class NotificationDetailsViewModel @AssistedInject constructor(
    @Assisted private val handle: SavedStateHandle,
    private val notificationsRepository: NotificationRepository
): ViewModel() {
    private val _notification = MutableLiveData<LceState<Notification>>()
    val notification: LiveData<LceState<Notification>> = _notification

    init {
        val id = handle.get<String>(NOTIFICATION_DETAILS_KEY)
        if (id != null) {
            loadNotification(id)
        }
    }

    fun loadNotification(id: String) = viewModelScope.launch {
        val notification = notificationsRepository.getNotificationById(id)
        _notification.postValue(notification)
    }

    @AssistedInject.Factory
    interface Factory : ViewModelAssistedFactory<NotificationDetailsViewModel>
}