package com.kyleriedemann.drinkingbuddy.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.kyleriedemann.drinkingbuddy.databinding.FragmentNotificationDetailsBinding

class NotificationDetailFragment : Fragment() {
    private lateinit var binding: FragmentNotificationDetailsBinding

    private val notificationArgs: NotificationDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotificationDetailsBinding.inflate(layoutInflater, container, false)
        binding.notification = notificationArgs.notification
        return binding.root
    }
}