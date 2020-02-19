package com.kyleriedemann.drinkingbuddy.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kyleriedemann.drinkingbuddy.data.models.Reading
import com.kyleriedemann.drinkingbuddy.databinding.DashboardItemBinding

class DashboardAdapter(private val viewModel: DashboardViewModel) :
        ListAdapter<Reading, DashboardAdapter.ViewHolder>(ReadingDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(viewModel, item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder.from(parent)

    class ViewHolder private constructor(private val binding: DashboardItemBinding) :
            RecyclerView.ViewHolder(binding.root) {

        fun bind(viewModel: DashboardViewModel, item: Reading) {
            binding.viewModel = viewModel
            binding.reading = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup) : ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = DashboardItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class ReadingDiffCallback : DiffUtil.ItemCallback<Reading>() {
    override fun areItemsTheSame(oldItem: Reading, newItem: Reading): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Reading, newItem: Reading): Boolean {
        return oldItem == newItem
    }
}