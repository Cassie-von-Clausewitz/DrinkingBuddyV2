package com.kyleriedemann.drinkingbuddy.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import com.kyleriedemann.drinkingbuddy.R
import com.kyleriedemann.drinkingbuddy.common.ui.BaseFragment
import com.kyleriedemann.drinkingbuddy.databinding.FragmentNotificationDetailsBinding

class NotificationDetailFragment : BaseFragment<NotificationDetailsViewModel, FragmentNotificationDetailsBinding>() {
    override val viewModel: NotificationDetailsViewModel by viewModels { viewModelFactory }
    override val layoutId: Int = R.layout.fragment_notification_details

    private val notificationArgs: NotificationDetailFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding.notification = notificationArgs.notification
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.notificationTitle.transitionName = notificationArgs.notification.id
        binding.notificationMessage.transitionName = notificationArgs.notification.time.toString()
        viewModel.markNotificationRead(notificationArgs.notification)
    }
}