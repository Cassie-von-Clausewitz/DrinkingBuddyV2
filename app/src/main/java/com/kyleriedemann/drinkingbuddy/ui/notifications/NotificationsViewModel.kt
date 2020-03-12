package com.kyleriedemann.drinkingbuddy.ui.notifications

import androidx.lifecycle.*
import com.kyleriedemann.drinkingbuddy.data.LceState
import com.kyleriedemann.drinkingbuddy.data.models.Notification
import com.kyleriedemann.drinkingbuddy.data.source.NotificationRepository
import com.kyleriedemann.drinkingbuddy.di.ViewModelAssistedFactory
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.launch

// todo get rid of refresh here and make these local only
class NotificationsViewModel @AssistedInject constructor(
    @Assisted private val handle: SavedStateHandle,
    private val notificationsRepository: NotificationRepository
): ViewModel() {

    private val _items = MutableLiveData<List<Notification>>().apply { value = emptyList() }
    val items: LiveData<List<Notification>> = notificationsRepository.getLiveNotifications()

    private val _loading = MutableLiveData<Boolean>(false)
    val loading: LiveData<Boolean> = _loading

    private val _errors = MutableLiveData<Exception>()
    val errors: LiveData<Exception> = _errors

    private val _navigate = MutableLiveData<Notification?>()
    val navigate: LiveData<Notification?> = _navigate

    val empty: LiveData<Boolean> = Transformations.map(_items) {
        it.isEmpty()
    }

    private fun loadNotifications() = viewModelScope.launch {
        when (val notifications = notificationsRepository.getNotifications()) {
            is LceState.Success -> {
                _items.postValue(notifications.data)
                _loading.postValue(false)
                clearError()
            }
            is LceState.Error -> {
                _errors.postValue(notifications.exception)
                _loading.postValue(false)
            }
            LceState.Loading -> {
                _loading.postValue(true)
                clearError()
            }
        }
    }

    fun markNotificationRead(notification: Notification) = viewModelScope.launch {
        // todo launch a detail view for these
        notificationsRepository.updateNotification(notification.copy(read = true))
        loadNotifications()
    }

    fun openDetails(notification: Notification) = viewModelScope.launch {
        notificationsRepository.updateNotification(notification.copy(read = true))
        _navigate.postValue(notification)
    }

    fun clearError() = _errors.postValue(null)

    fun refresh() = loadNotifications()

    fun clearNavigation() = _navigate.postValue(null)

    @AssistedInject.Factory
    interface Factory : ViewModelAssistedFactory<NotificationsViewModel>
}
