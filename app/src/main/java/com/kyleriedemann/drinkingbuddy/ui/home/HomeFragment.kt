package com.kyleriedemann.drinkingbuddy.ui.home

import android.Manifest
import android.app.AlertDialog
import androidx.annotation.StringRes
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.observe
import com.eazypermissions.common.model.PermissionResult
import com.eazypermissions.livedatapermission.PermissionManager
import com.kyleriedemann.drinkingbuddy.R
import com.kyleriedemann.drinkingbuddy.common.ui.BaseFragment
import com.kyleriedemann.drinkingbuddy.databinding.FragmentHomeBinding
import timber.log.Timber

const val REQUEST_ID = 420

class HomeFragment : BaseFragment<HomeViewModel, FragmentHomeBinding>(), PermissionManager.PermissionObserver {
    override val viewModel: HomeViewModel by viewModels { viewModelFactory }
    override val layoutId: Int = R.layout.fragment_home

    override fun onStart() {
        super.onStart()
        PermissionManager.requestPermissions(
            this,
            REQUEST_ID,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH)
    }

    override fun setupObserver(permissionResultLiveData: LiveData<PermissionResult>) {
        if (view == null) return
        permissionResultLiveData.observe(viewLifecycleOwner) {
            Timber.v("Permissions response $it")
            if (it.requestCode != REQUEST_ID) return@observe
            when (it) {
                is PermissionResult.PermissionGranted -> {
                    Timber.v("Permissions Granted")
                    viewModel.permissionsGranted()
                }
                is PermissionResult.PermissionDenied -> {
                    Timber.w("Permissions Denied [${it.deniedPermissions}]")
                    permissionsDenied()
                }
                is PermissionResult.PermissionDeniedPermanently -> {
                    Timber.w("Permissions Denied Forever [${it.permanentlyDeniedPermissions}]")
                    permissionsDeniedForever()
                }
                is PermissionResult.ShowRational -> {
                    Timber.v("Show Rational")
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
}
