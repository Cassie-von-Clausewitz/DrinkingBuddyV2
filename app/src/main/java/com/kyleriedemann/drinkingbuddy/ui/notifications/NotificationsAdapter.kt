package com.kyleriedemann.drinkingbuddy.ui.notifications

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kyleriedemann.drinkingbuddy.ui.notifications.NotificationsAdapter.ViewHolder
import com.kyleriedemann.drinkingbuddy.data.models.Notification
import com.kyleriedemann.drinkingbuddy.databinding.NotificationItemBinding

class NotificationsAdapter(private val navController: NavController) :
    ListAdapter<Notification, ViewHolder>(NotificationsDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        holder.bind(navController, item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder.from(parent)

    class ViewHolder private constructor(private val binding: NotificationItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(navController: NavController, item: Notification) {
            binding.notification = item
            binding.executePendingBindings()

            binding.notificationTitle.transitionName = item.id
            binding.notificationMessage.transitionName = item.time.toString()

            binding.notificationListItemCard.setOnClickListener {
                val action = NotificationsFragmentDirections.actionNavigationNotificationsToNotificationDetailFragment(item)
                val extras = FragmentNavigatorExtras(
                    binding.notificationTitle to item.id,
                    binding.notificationMessage to item.time.toString()
                )

                navController.navigate(action, extras)
            }
        }

        companion object {
            fun from(parent: ViewGroup) : ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = NotificationItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class NotificationsDiffCallback : DiffUtil.ItemCallback<Notification>() {
    override fun areItemsTheSame(oldItem: Notification, newItem: Notification): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Notification, newItem: Notification): Boolean {
        return oldItem == newItem
    }
}