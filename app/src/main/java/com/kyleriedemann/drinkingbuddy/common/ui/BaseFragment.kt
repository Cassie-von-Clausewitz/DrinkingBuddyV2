package com.kyleriedemann.drinkingbuddy.common.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.ViewModel
import com.google.android.material.snackbar.Snackbar
import com.kyleriedemann.drinkingbuddy.di.ViewModelFactory
import dagger.android.support.DaggerFragment
import javax.inject.Inject

abstract class BaseFragment<V: ViewModel, B: ViewDataBinding> : DaggerFragment() {
    @Inject lateinit var viewModelFactory: ViewModelFactory

    protected abstract val viewModel: V

    protected lateinit var binding: B

    @get:LayoutRes
    protected abstract val layoutId: Int

    private var errorSnackBar: Snackbar? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        binding.lifecycleOwner = this
        binding.setVariable(BR.viewModel, viewModel)
        return binding.root
    }

    private fun showError(error: Throwable) {
        showError(error.message ?: "")
    }

    private fun showError(message: String) {
        errorSnackBar = Snackbar.make(binding.root, message, Snackbar.LENGTH_INDEFINITE).apply {
            show()
        }
    }

    private fun dismissErrorIfShown() {
        errorSnackBar?.dismiss()
    }
}