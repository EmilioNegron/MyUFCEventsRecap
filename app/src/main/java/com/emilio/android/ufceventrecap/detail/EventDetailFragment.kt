package com.emilio.android.ufceventrecap.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.emilio.android.ufceventrecap.databinding.FragmentEventDetailBinding
import com.google.firebase.analytics.FirebaseAnalytics

class EventDetailFragment : Fragment() {
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val application = requireNotNull(activity).application
        firebaseAnalytics = FirebaseAnalytics.getInstance(application)
        val binding = FragmentEventDetailBinding.inflate(inflater)
        binding.lifecycleOwner = this

        val eventSelected = EventDetailFragmentArgs.fromBundle(arguments!!).selectedEvent
        firebaseAnalytics.setUserProperty("EventSelectedProperty", eventSelected.strEvent)
        val viewModelFactory = EventDetailViewModelFactory(eventSelected, application)
        binding.viewModel = ViewModelProvider(
                this, viewModelFactory).get(EventDetailViewModel::class.java)

        return binding.root
    }
}