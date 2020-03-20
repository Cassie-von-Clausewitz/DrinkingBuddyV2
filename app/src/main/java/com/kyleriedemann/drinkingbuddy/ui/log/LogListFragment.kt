package com.kyleriedemann.drinkingbuddy.ui.log

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.kyleriedemann.drinkingbuddy.R
import com.kyleriedemann.drinkingbuddy.common.ui.BaseFragment
import com.kyleriedemann.drinkingbuddy.databinding.FragmentLogListBinding
import kotlinx.coroutines.flow.collect

class LogListFragment : BaseFragment<LogListViewModel, FragmentLogListBinding>() {
    override val viewModel: LogListViewModel by viewModels { viewModelFactory }
    override val layoutId = R.layout.fragment_log_list

    private lateinit var listAdapter: LogAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListAdapter()

        lifecycleScope.launchWhenResumed {
            viewModel.filteredItems().collect {
                listAdapter.submitList(it)
            }
        }

        binding.fab.setOnClickListener {
            binding.fab.isExpanded = true
        }

        binding.notificationsList.addOnScrollListener(object:RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when (newState) {
                    RecyclerView.SCROLL_STATE_DRAGGING -> binding.fab.isExpanded = false
                }
            }
        })

        lifecycleScope.launchWhenResumed {
            viewModel.levelSettings.collect { filter ->
                binding.verboseChip.isChecked = filter.verbose
                binding.debugChip.isChecked = filter.debug
                binding.infoChip.isChecked = filter.info
                binding.warnChip.isChecked = filter.warn
                binding.errorChip.isChecked = filter.error
                binding.assertChip.isChecked = filter.assert
                binding.unknownChip.isChecked = filter.unknown
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.tagSettings.collect { list ->
                binding.chipGroup.removeAllViews()
                list.forEach {
                    val chip = Chip(binding.chipGroup.context)
                    chip.isCheckable = true
                    chip.text = it.tag
                    chip.isChecked = it.selected
                    chip.setOnClickListener { _ ->
                        viewModel.toggleTagFilter(it)
                    }
                    binding.chipGroup.addView(chip)
                }
            }
        }
    }

    private fun setupListAdapter() {
        listAdapter = LogAdapter(findNavController())
        binding.notificationsList.adapter = listAdapter
    }
}