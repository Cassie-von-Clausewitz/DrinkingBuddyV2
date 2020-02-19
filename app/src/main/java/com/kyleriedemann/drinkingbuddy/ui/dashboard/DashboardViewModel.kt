package com.kyleriedemann.drinkingbuddy.ui.dashboard

import androidx.lifecycle.*
import com.kyleriedemann.drinkingbuddy.data.LceState
import com.kyleriedemann.drinkingbuddy.data.models.Reading
import com.kyleriedemann.drinkingbuddy.data.source.ReadingRepository
import com.kyleriedemann.drinkingbuddy.di.ViewModelAssistedFactory
import com.snakydesign.livedataextensions.map
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.launch
import java.lang.Exception

class DashboardViewModel @AssistedInject constructor(
    @Assisted private val handle: SavedStateHandle,
    private val readingRepository: ReadingRepository
) : ViewModel() {

    private val _items = MutableLiveData<List<Reading>>().apply { value = emptyList() }
    val items: LiveData<List<Reading>> = _items

    private val _loading = MutableLiveData<Boolean>(false)
    val loading: LiveData<Boolean> = _loading

    private val _errors = MutableLiveData<Exception>()
    val errors: LiveData<Exception> = _errors

    val empty: LiveData<Boolean> = _items.map { it.isEmpty() }

    private fun loadReadings() = viewModelScope.launch {
        when (val readings = readingRepository.getReadings()) {
            is LceState.Success -> {
                _items.postValue(readings.data)
                _loading.postValue(false)
                clearError()
            }
            is LceState.Error -> {
                _errors.postValue(readings.exception)
                _loading.postValue(false)
            }
            is LceState.Loading -> {
                _loading.postValue(true)
                clearError()
            }
        }
    }

    fun clearError() = _errors.postValue(null)

    fun refresh() = loadReadings()

    fun displayData(reading: Reading)
            = "Predicted: ${reading.prediction}, Result: ${reading.result}"

    @AssistedInject.Factory
    interface Factory : ViewModelAssistedFactory<DashboardViewModel>
}
