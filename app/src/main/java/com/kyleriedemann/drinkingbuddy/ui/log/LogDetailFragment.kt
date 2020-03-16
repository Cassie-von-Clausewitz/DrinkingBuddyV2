package com.kyleriedemann.drinkingbuddy.ui.log

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.kyleriedemann.drinkingbuddy.databinding.FragmentLogDetailsBinding

class LogDetailFragment : Fragment() {
    private lateinit var binding: FragmentLogDetailsBinding

    private val logDetailArgs: LogDetailFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLogDetailsBinding.inflate(layoutInflater, container, false)
        binding.lifecycleOwner = this
        binding.log = logDetailArgs.log
        return binding.root
    }
}