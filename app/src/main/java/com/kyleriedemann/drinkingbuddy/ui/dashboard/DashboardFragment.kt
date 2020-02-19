package com.kyleriedemann.drinkingbuddy.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import com.kyleriedemann.drinkingbuddy.R
import com.kyleriedemann.drinkingbuddy.common.ui.BaseFragment
import com.kyleriedemann.drinkingbuddy.databinding.FragmentDashboardBinding
import com.kyleriedemann.drinkingbuddy.ui.notifications.NotificationsViewModel

class DashboardFragment : BaseFragment<DashboardViewModel, FragmentDashboardBinding>() {
    override val viewModel: DashboardViewModel by viewModels { viewModelFactory }
    override val layoutId: Int = R.layout.fragment_dashboard

    private lateinit var listAdapter: DashboardAdapter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupListAdapter()
        viewModel.refresh()

        viewModel.errors.observe(viewLifecycleOwner) {
            showError(it)
        }
    }

    private fun setupListAdapter() {
        listAdapter = DashboardAdapter(viewModel)
        binding.readingsList.adapter = listAdapter
    }
}
