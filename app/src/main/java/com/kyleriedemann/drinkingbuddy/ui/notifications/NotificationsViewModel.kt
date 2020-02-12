package com.kyleriedemann.drinkingbuddy.ui.notifications

import androidx.lifecycle.*
import com.kyleriedemann.drinkingbuddy.data.Result
import com.kyleriedemann.drinkingbuddy.data.models.Notification
import com.kyleriedemann.drinkingbuddy.data.source.NotificationRepository
import com.kyleriedemann.drinkingbuddy.di.ViewModelAssistedFactory
import com.kyleriedemann.drinkingbuddy.ui.home.HomeViewModel
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

    val empty: LiveData<Boolean> = Transformations.map(_items) {
        it.isEmpty()
    }

    private fun loadNotifications() = viewModelScope.launch {
        when (val notifications = notificationsRepository.getNotifications()) {
            is Result.Success -> {
                _items.postValue(notifications.data)
                _loading.postValue(false)
                clearError()
            }
            is Result.Error -> {
                _errors.postValue(notifications.exception)
                _loading.postValue(false)
                clearError()
            }
            Result.Loading -> {
                _loading.postValue(true)
            }
        }
    }

    fun markNotificationRead(notification: Notification) = viewModelScope.launch {
        // todo launch a detail view for these
        notificationsRepository.updateNotification(notification.copy(read = true))
        loadNotifications()
    }

    fun clearError() = _errors.postValue(null)

    fun refresh() {
        loadNotifications()
    }

    @AssistedInject.Factory
    interface Factory : ViewModelAssistedFactory<NotificationsViewModel>
}
