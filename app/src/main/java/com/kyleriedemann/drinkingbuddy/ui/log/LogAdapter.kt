package com.kyleriedemann.drinkingbuddy.ui.log

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kyleriedemann.drinkingbuddy.data.models.Log
import com.kyleriedemann.drinkingbuddy.databinding.LogItemBinding
import com.kyleriedemann.drinkingbuddy.ui.common.logLevelToColor
import timber.log.Timber

class LogAdapter(private val navController: NavController):
        ListAdapter<Log, LogAdapter.ViewHolder>(LogDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        holder.bind(navController, item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder.from(parent)

    class ViewHolder private constructor(private val binding: LogItemBinding):
        RecyclerView.ViewHolder(binding.root) {

        fun bind(navController: NavController, item: Log) {
            binding.log = item
            binding.executePendingBindings()

            binding.logListItemCard.setOnClickListener {
                val action = LogListFragmentDirections.actionLogListFragmentToLogDetailFragment(item)
                navController.navigate(action)
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = LogItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class LogDiffCallback: DiffUtil.ItemCallback<Log>() {
    override fun areItemsTheSame(oldItem: Log, newItem: Log): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Log, newItem: Log): Boolean {
        return oldItem == newItem
    }
}