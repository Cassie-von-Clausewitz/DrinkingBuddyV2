package com.kyleriedemann.drinkingbuddy.ui.log

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.kyleriedemann.drinkingbuddy.R
import com.kyleriedemann.drinkingbuddy.common.ui.BaseFragment
import com.kyleriedemann.drinkingbuddy.data.models.LogLevel
import com.kyleriedemann.drinkingbuddy.data.models.LogTag
import com.kyleriedemann.drinkingbuddy.databinding.FragmentLogListBinding
import com.kyleriedemann.drinkingbuddy.ui.common.logLevelToColor
import kotlinx.android.synthetic.main.fragment_log_list.view.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

class LogListFragment : BaseFragment<LogListViewModel, FragmentLogListBinding>() {
    override val viewModel: LogListViewModel by viewModels { viewModelFactory }
    override val layoutId = R.layout.fragment_log_list

    private lateinit var listAdapter: LogAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListAdapter()

//        lifecycleScope.launchWhenResumed {
//            viewModel.items.collect {
//                listAdapter.submitList(it)
//            }
//        }

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

//        lifecycleScope.launchWhenResumed {
//            viewModel.levels.collect { list ->
//                list.forEach {
//                    val chip = Chip(binding.chipGroup.context)
//                    chip.text = it.name
//                    val color = ContextCompat.getColor(chip.context, logLevelToColor(it))
//                    chip.chipBackgroundColor = ColorStateList.valueOf(color)
//                    binding.chipGroup.addView(chip)
//                }
//            }
//        }

        lifecycleScope.launchWhenResumed {
            viewModel.levelSettings.collect { filter ->
                binding.levelsText.text = filter.toString()
                Timber.v("LevelFilter $filter")
                Timber.v("Selections [verbose: ${binding.verboseChip.isChecked}, debug: ${binding.debugChip.isChecked}, info: ${binding.infoChip.isChecked}, warn: ${binding.warnChip.isChecked}, error: ${binding.errorChip.isChecked}, assert: ${binding.assertChip.isChecked}, unknown: ${binding.unknownChip.isChecked}]")
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
                binding.tagsText.text = list.toString()
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