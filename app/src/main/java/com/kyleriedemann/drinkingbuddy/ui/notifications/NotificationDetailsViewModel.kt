package com.kyleriedemann.drinkingbuddy.ui.notifications

import androidx.lifecycle.*
import com.kyleriedemann.drinkingbuddy.data.LceState
import com.kyleriedemann.drinkingbuddy.data.models.Notification
import com.kyleriedemann.drinkingbuddy.data.source.NotificationRepository
import com.kyleriedemann.drinkingbuddy.di.ViewModelAssistedFactory
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.launch

class NotificationDetailsViewModel @AssistedInject constructor(
    @Assisted private val handle: SavedStateHandle,
    private val notificationsRepository: NotificationRepository
): ViewModel() {
    fun markNotificationRead(notification: Notification) = viewModelScope.launch {
        notificationsRepository.markRead(notification.id, true)
    }

    @AssistedInject.Factory
    interface Factory : ViewModelAssistedFactory<NotificationDetailsViewModel>
}