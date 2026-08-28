package com.pinehotel.hospitality.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.pinehotel.hospitality.databinding.FragmentConciergeBinding
import com.pinehotel.hospitality.utils.FocusUtils
import com.pinehotel.hospitality.viewmodels.MainViewModel
import com.pinehotel.hospitality.utils.NotificationUtils
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ConciergeFragment : Fragment() {

    private var _binding: FragmentConciergeBinding? = null
    private val binding get() = _binding!!
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConciergeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val types = listOf("Local Attractions", "Restaurant Recommendation", "Tour Booking", "Event Arrangement")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, types)
        binding.actRequestType.setAdapter(adapter)

        binding.btnSubmit.setOnFocusChangeListener { view, hasFocus ->
            FocusUtils.applyScaleAnimation(view, hasFocus)
        }

        binding.btnSubmit.setOnClickListener {
            val type = binding.actRequestType.text.toString()
            val description = binding.tietDescription.text.toString()
            if (type.isNotEmpty()) {
                mainViewModel.submitServiceRequest("concierge", 0, type, description)
                binding.tietDescription.text?.clear()
            } else {
                Toast.makeText(context, "Please select a request type", Toast.LENGTH_SHORT).show()
            }
        }

        observeSubmission()
    }

    private fun observeSubmission() {
        viewLifecycleOwner.lifecycleScope.launch {
            mainViewModel.requestSuccess.collectLatest { success ->
                success?.let {
                    if (it) {
                        NotificationUtils.showSuccessNotification(binding.root, "Concierge request submitted successfully")
                    }
                    mainViewModel.resetRequestState()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}