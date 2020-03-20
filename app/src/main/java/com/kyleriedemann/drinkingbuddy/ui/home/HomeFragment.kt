package com.kyleriedemann.drinkingbuddy.ui.home

import android.Manifest
import android.app.AlertDialog
import androidx.annotation.StringRes
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.observe
import com.eazypermissions.common.model.PermissionResult
import com.eazypermissions.livedatapermission.PermissionManager
import com.github.ajalt.timberkt.Timber.v
import com.kyleriedemann.drinkingbuddy.R
import com.kyleriedemann.drinkingbuddy.common.ui.BaseFragment
import com.kyleriedemann.drinkingbuddy.databinding.FragmentHomeBinding

const val REQUEST_ID = 420

class HomeFragment : BaseFragment<HomeViewModel, FragmentHomeBinding>(), PermissionManager.PermissionObserver {
    override val viewModel: HomeViewModel by viewModels { viewModelFactory }
    override val layoutId: Int = R.layout.fragment_home

    override fun onStart() {
        super.onStart()
        requestPermissions()
        setupSdkObservers()
        viewModel.sendWelcomeNotification()
    }

    /**
     * Starts permission request and receives results in [setupObserver]
     */
    private fun requestPermissions() {
        PermissionManager.requestPermissions(
            this,
            REQUEST_ID,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH)
    }

    /**
     * Start observing the data coming from the SDK exposed via the [HomeViewModel]
     */
    private fun setupSdkObservers() {
        viewModel.connected.observe(viewLifecycleOwner) {
            binding.textHome.text = "$it"
        }

        viewModel.reading.observe(viewLifecycleOwner) {
            binding.textHome.text = "$it"
        }

        viewModel.error.observe(viewLifecycleOwner) {
            showError(it.toString())
        }
    }

    /**
     * Sets up the [LiveData] Observer for permission results
     */
    @Suppress("EXPERIMENTAL_API_USAGE")
    override fun setupObserver(permissionResultLiveData: LiveData<PermissionResult>) {
        if (view == null) return
        permissionResultLiveData.observe(viewLifecycleOwner) {
            v { it.readableToString() }
            if (it.requestCode != REQUEST_ID) return@observe
            when (it) {
                is PermissionResult.PermissionGranted -> {
                    viewModel.permissionsGranted()
                }
                is PermissionResult.PermissionDenied -> {
                    permissionsDenied()
                }
                is PermissionResult.PermissionDeniedPermanently -> {
                    permissionsDeniedForever()
                }
                is PermissionResult.ShowRational -> {
                    showRational()
                }
            }
        }
    }

    private fun permissionsDenied() {
        showDialog(R.string.permissions_denied)
    }

    private fun permissionsDeniedForever() {
        showDialog(R.string.permissions_denied_forever)
    }

    private fun showRational() {
        showDialog(R.string.permissions_rational)
    }

    /**
     * Created a dialog with a message from a given string res. Used for explaining permission requests
     */
    private fun showDialog(@StringRes message: Int) {
        activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                setTitle(R.string.permissions_title)
                setMessage(message)
                setPositiveButton(R.string.ok) { _, _ -> }
            }
            builder.create().show()
        }
    }

    private fun PermissionResult.readableToString(): String = when(this) {
        is PermissionResult.PermissionGranted -> "PermissionGranted(requestCode: ${this.requestCode})"
        is PermissionResult.PermissionDenied -> "PermissionDenied(requestCode: ${this.requestCode}, deniedPermissions: ${this.deniedPermissions})"
        is PermissionResult.ShowRational -> "ShowRational(requestCode: ${this.requestCode})"
        is PermissionResult.PermissionDeniedPermanently -> "PermissionDeniedPermanently(requestCode: ${this.requestCode}, permanentlyDeniedPermissions: ${this.permanentlyDeniedPermissions})"
    }
}
