package com.emilio.android.ufceventrecap.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.emilio.android.ufceventrecap.databinding.FragmentEventDetailBinding

class EventDetailFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val application = requireNotNull(activity).application
        val binding = FragmentEventDetailBinding.inflate(inflater)
        binding.lifecycleOwner = this

        val eventSelected = EventDetailFragmentArgs.fromBundle(arguments!!).selectedEvent
        val viewModelFactory = EventDetailViewModelFactory(eventSelected, application)
        binding.viewModel = ViewModelProvider(
                this, viewModelFactory).get(EventDetailViewModel::class.java)

        return binding.root
    }
}