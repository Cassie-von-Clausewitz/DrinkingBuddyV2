package com.kyleriedemann.drinkingbuddy.data

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 */
sealed class LceState<out R> {

    data class Success<out T>(val data: T) : LceState<T>()
    data class Error(val exception: Exception) : LceState<Nothing>()
    object Loading : LceState<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
            Loading -> "Loading"
        }
    }
}

/**
 * `true` if [LceState] is of type [Success] & holds non-null [Success.data].
 */
val LceState<*>.succeeded
    get() = this is LceState.Success && data != null