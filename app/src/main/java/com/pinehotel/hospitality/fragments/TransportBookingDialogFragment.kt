package com.pinehotel.hospitality.fragments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.pinehotel.hospitality.R
import com.pinehotel.hospitality.databinding.DialogTransportBookingBinding
import com.pinehotel.hospitality.utils.FocusUtils
import com.pinehotel.hospitality.utils.NotificationUtils
import com.pinehotel.hospitality.utils.PreferenceManager
import com.pinehotel.hospitality.viewmodels.MainViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class TransportBookingDialogFragment : DialogFragment() {

    private var _binding: DialogTransportBookingBinding? = null
    private val binding get() = _binding!!
    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var preferenceManager: PreferenceManager

    private var serviceId: Int = -1
    private var serviceTitle: String = ""
    private var selectedSlot: String = ""
    
    private var passengers: Int = 2
    private var bags: Int = 3

    companion object {
        private const val ARG_ID = "service_id"
        private const val ARG_TITLE = "service_title"
        private const val ARG_SLOT = "selected_slot"

        fun newInstance(id: Int, title: String, slot: String): TransportBookingDialogFragment {
            val fragment = TransportBookingDialogFragment()
            val args = Bundle()
            args.putInt(ARG_ID, id)
            args.putString(ARG_TITLE, title)
            args.putString(ARG_SLOT, slot)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            serviceId = it.getInt(ARG_ID)
            serviceTitle = it.getString(ARG_TITLE) ?: ""
            selectedSlot = it.getString(ARG_SLOT) ?: "ASAP"
        }
        preferenceManager = PreferenceManager(requireContext())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        _binding = DialogTransportBookingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.let { window ->
            val displayMetrics = resources.displayMetrics
            val width = (displayMetrics.widthPixels * 0.6).toInt().coerceAtLeast(600)
            window.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUIBasedOnService()
        loadGuestInfo()
        setupInteractions()
    }

    private fun setupUIBasedOnService() {
        val title = serviceTitle.lowercase()
        binding.tvFormTitle.text = serviceTitle

        when {
            title.contains("pickup") -> {
                // Airport Pickup
                binding.tvField1Label.text = "DATE & TIME"
                binding.tvField1Value.text = "28 Aug $selectedSlot"

                binding.tvField2Label.text = "AIRLINE"
                binding.tvField2Value.text = "IndiGo"

                binding.tvField3Label.text = "PASSENGERS"
                binding.tvField3Value.text = passengers.toString()
                
                binding.llField4Container.visibility = View.VISIBLE
                binding.tvField4Label.text = "BAGS"
                binding.tvField4Value.text = bags.toString()

                binding.etOptionalField.hint = "Flight number (optional)"
            }
            title.contains("drop") -> {
                // Airport Drop
                binding.tvField1Label.text = "DATE & TIME"
                binding.tvField1Value.text = "28 Aug $selectedSlot"

                binding.tvField2Label.text = "FLIGHT TIME"
                binding.tvField2Value.text = "3:30 PM (Suggest)"

                binding.tvField3Label.text = "PASSENGERS"
                binding.tvField3Value.text = passengers.toString()
                
                binding.llField4Container.visibility = View.VISIBLE
                binding.tvField4Label.text = "BAGS"
                binding.tvField4Value.text = bags.toString()

                binding.etOptionalField.hint = "Flight number (optional)"
            }
            title.contains("taxi") -> {
                // Taxi Service
                binding.tvField1Label.text = "PICKUP POINT"
                binding.tvField1Value.text = "Hotel Main Lobby"

                binding.tvField2Label.text = "DESTINATION"
                binding.tvField2Value.text = "City Center"

                binding.tvField3Label.text = "PICKUP TIME"
                binding.tvField3Value.text = selectedSlot
                
                binding.llField4Container.visibility = View.VISIBLE
                binding.tvField4Label.text = "PASSENGERS"
                binding.tvField4Value.text = passengers.toString()

                binding.etOptionalField.hint = "Custom address (optional)"
            }
            title.contains("shuttle") -> {
                // Shuttle Service
                binding.tvField1Label.text = "STOP"
                binding.tvField1Value.text = "Town Center"

                binding.tvField2Label.text = "DEPARTURE"
                binding.tvField2Value.text = selectedSlot

                binding.tvField3Label.text = "RETURN"
                binding.tvField3Value.text = "Yes"
                
                binding.llField4Container.visibility = View.VISIBLE
                binding.tvField4Label.text = "PASSENGERS"
                binding.tvField4Value.text = passengers.toString()

                binding.etOptionalField.isEnabled = false
                binding.etOptionalField.setText("Remote-navigable only")
                binding.etOptionalField.setTextColor(Color.parseColor("#40D4AF37"))
            }
        }
    }

    private fun loadGuestInfo() {
        lifecycleScope.launch {
            val room = preferenceManager.roomNumberFlow.first() ?: "205"
            binding.tvGuestInfo.text = "Room $room - Dhivya S"
        }
    }

    private fun setupInteractions() {
        val focusableViews = listOf(
            binding.llField1, 
            binding.llField2, 
            binding.llField3, 
            binding.llField4, 
            binding.etOptionalField, 
            binding.btnConfirm
        )
        
        focusableViews.forEach { v ->
            v.isFocusable = true
            // Ensure child views don't block focus
            if (v is ViewGroup) {
                v.descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS
            }
            v.setOnFocusChangeListener { view, hasFocus ->
                FocusUtils.applyScaleAnimation(view, hasFocus)
            }
        }

        // Logic for +/- counters
        binding.llField3.setOnClickListener {
            if (binding.tvField3Label.text == "Passengers") {
                passengers = if (passengers >= 6) 1 else passengers + 1
                binding.tvField3Value.text = passengers.toString()
            }
        }
        
        binding.llField4.setOnClickListener {
            if (binding.tvField4Label.text == "Bags") {
                bags = if (bags >= 10) 0 else bags + 1
                binding.tvField4Value.text = bags.toString()
            } else if (binding.tvField4Label.text == "Passengers") {
                passengers = if (passengers >= 6) 1 else passengers + 1
                binding.tvField4Value.text = passengers.toString()
            }
        }

        binding.btnConfirm.setOnClickListener {
            val details = "Slot: $selectedSlot, Pax: $passengers, Bags: $bags. ${binding.etOptionalField.text}"
            mainViewModel.submitServiceRequest("transport", serviceId, serviceTitle, details)
            
            // Simplified Flow: Close immediately and show notification
            NotificationUtils.showSuccessNotification(requireActivity().findViewById(android.R.id.content), "Your service is booked successfully")
            dismiss()
        }

        // Set explicit navigation for D-pad to ensure Confirm is reachable
        binding.etOptionalField.nextFocusDownId = binding.btnConfirm.id
        binding.btnConfirm.nextFocusUpId = binding.etOptionalField.id

        binding.btnConfirm.post {
            binding.btnConfirm.requestFocus()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
