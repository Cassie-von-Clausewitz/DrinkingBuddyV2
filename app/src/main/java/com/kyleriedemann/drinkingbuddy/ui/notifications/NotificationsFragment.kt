package com.kyleriedemann.drinkingbuddy.ui.notifications

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.kyleriedemann.drinkingbuddy.R
import com.kyleriedemann.drinkingbuddy.common.ui.BaseFragment
import com.kyleriedemann.drinkingbuddy.databinding.FragmentNotificationsBinding

class NotificationsFragment : BaseFragment<NotificationsViewModel, FragmentNotificationsBinding>() {
    override val viewModel: NotificationsViewModel by viewModels { viewModelFactory }
    override val layoutId: Int = R.layout.fragment_notifications

    private lateinit var listAdapter: NotificationsAdapter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupListAdapter()
        viewModel.refresh()

        viewModel.errors.observe(viewLifecycleOwner) {
            showError(it)
        }

        viewModel.navigate.observe(viewLifecycleOwner) {
            findNavController().navigate(NotificationsFragmentDirections.actionNavigationNotificationsToNotificationDetailFragment(it))
        }
    }

    private fun setupListAdapter() {
        listAdapter = NotificationsAdapter(viewModel)
        binding.notificationsList.adapter = listAdapter
    }
}
