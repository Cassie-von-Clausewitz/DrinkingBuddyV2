package com.kyleriedemann.drinkingbuddy.ui.log

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.kyleriedemann.drinkingbuddy.R
import com.kyleriedemann.drinkingbuddy.common.ui.BaseFragment
import com.kyleriedemann.drinkingbuddy.databinding.FragmentLogListBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LogListFragment : BaseFragment<LogListViewModel, FragmentLogListBinding>() {
    override val viewModel: LogListViewModel by viewModels { viewModelFactory }
    override val layoutId = R.layout.fragment_log_list

    private lateinit var listAdapter: LogAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListAdapter()

        viewModel.viewModelScope.launch {
            viewModel.items.collect {
                listAdapter.submitList(it)
            }
        }
    }

    private fun setupListAdapter() {
        listAdapter = LogAdapter(findNavController())
        binding.notificationsList.adapter = listAdapter
    }
}