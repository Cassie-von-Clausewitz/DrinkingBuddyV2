package com.kyleriedemann.drinkingbuddy.ui.home

import android.os.Bundle
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryOwner
import com.kyleriedemann.drinkingbuddy.di.ViewModelAssistedFactory
import com.kyleriedemann.drinkingbuddy.sdk.BacTrackSdk
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import dagger.MapKey
import javax.inject.Inject
import kotlin.reflect.KClass

class HomeViewModel @AssistedInject constructor (
    @Assisted private val handle: SavedStateHandle,
    private val sdk: BacTrackSdk
) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    @AssistedInject.Factory
    interface Factory : ViewModelAssistedFactory<HomeViewModel>
}
