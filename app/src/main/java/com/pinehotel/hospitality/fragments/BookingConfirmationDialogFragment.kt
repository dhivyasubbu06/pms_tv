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
import com.bumptech.glide.Glide
import com.pinehotel.hospitality.R
import com.pinehotel.hospitality.databinding.DialogBookingConfirmationBinding
import com.pinehotel.hospitality.utils.FocusUtils
import com.pinehotel.hospitality.utils.PreferenceManager
import com.pinehotel.hospitality.utils.PriceUtils
import com.pinehotel.hospitality.utils.UrlUtils
import com.pinehotel.hospitality.viewmodels.MainViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BookingConfirmationDialogFragment : DialogFragment() {

    private var _binding: DialogBookingConfirmationBinding? = null
    private val binding get() = _binding!!
    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var preferenceManager: PreferenceManager

    private var serviceId: Int = -1
    private var serviceTitle: String = ""
    private var servicePrice: Double = 0.0
    private var serviceType: String = ""
    private var selectedSlot: String = ""
    private var imageUrl: String? = null

    companion object {
        private const val ARG_ID = "service_id"
        private const val ARG_TITLE = "service_title"
        private const val ARG_PRICE = "service_price"
        private const val ARG_TYPE = "service_type"
        private const val ARG_SLOT = "selected_slot"
        private const val ARG_IMAGE = "image_url"

        fun newInstance(id: Int?, title: String?, price: Double?, type: String, slot: String, image: String?): BookingConfirmationDialogFragment {
            val fragment = BookingConfirmationDialogFragment()
            val args = Bundle()
            args.putInt(ARG_ID, id ?: 0)
            args.putString(ARG_TITLE, title ?: "")
            args.putDouble(ARG_PRICE, price ?: 0.0)
            args.putString(ARG_TYPE, type)
            args.putString(ARG_SLOT, slot)
            args.putString(ARG_IMAGE, image)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            serviceId = it.getInt(ARG_ID)
            serviceTitle = it.getString(ARG_TITLE) ?: ""
            servicePrice = it.getDouble(ARG_PRICE)
            serviceType = it.getString(ARG_TYPE) ?: "spa"
            selectedSlot = it.getString(ARG_SLOT) ?: ""
            imageUrl = it.getString(ARG_IMAGE)
        }
        preferenceManager = PreferenceManager(requireContext())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        _binding = DialogBookingConfirmationBinding.inflate(inflater, container, false)
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

        binding.tvServiceTitle.text = serviceTitle
        binding.tvServiceCategory.text = serviceType.uppercase()
        binding.tvTimeSlotLabel.text = selectedSlot
        val displayPrice = PriceUtils.getDynamicPriceInt(servicePrice.toInt(), serviceType, serviceTitle)
        binding.tvPriceLabel.text = "₹$displayPrice"

        lifecycleScope.launch {
            val room = preferenceManager.roomNumberFlow.first() ?: "---"
            binding.tvRoomLabel.text = "Room $room"
        }

        val fullImageUrl = UrlUtils.getFullImageUrl(imageUrl)
        if (serviceType == "transport") {
            binding.ivServiceIcon.visibility = View.GONE
            binding.tvEmoji.visibility = View.VISIBLE
            binding.tvEmoji.text = when {
                serviceTitle.contains("Airport Pickup", ignoreCase = true) == true -> "🚗"
                serviceTitle.contains("Airport Drop", ignoreCase = true) == true -> "🚖"
                serviceTitle.contains("Taxi", ignoreCase = true) == true -> "🚕"
                serviceTitle.contains("Shuttle", ignoreCase = true) == true -> "🚐"
                else -> "🚗"
            }
        } else {
            binding.ivServiceIcon.visibility = View.VISIBLE
            binding.tvEmoji.visibility = View.GONE
            Glide.with(this)
                .load(fullImageUrl)
                .placeholder(R.drawable.ic_room_service)
                .into(binding.ivServiceIcon)
        }

        // TV Focus
        val focusableViews = listOf(binding.btnClose, binding.btnCancel, binding.btnConfirm)
        focusableViews.forEach { v ->
            v.isFocusable = true
            v.setOnFocusChangeListener { view, hasFocus ->
                FocusUtils.applyScaleAnimation(view, hasFocus)
            }
        }

        binding.btnClose.setOnClickListener { dismiss() }
        binding.btnCancel.setOnClickListener { dismiss() }

        binding.btnConfirm.setOnClickListener {
            mainViewModel.submitServiceRequest(serviceType, serviceId, serviceTitle, selectedSlot)
            // Button is disabled to prevent multiple clicks
            binding.btnConfirm.isEnabled = false
        }

        observeSubmission()

        binding.btnConfirm.post {
            binding.btnConfirm.requestFocus()
        }
    }

    private fun observeSubmission() {
        viewLifecycleOwner.lifecycleScope.launch {
            mainViewModel.requestSuccess.collectLatest { success ->
                success?.let {
                    if (it) {
                        showSuccessState()
                    } else {
                        binding.btnConfirm.isEnabled = true
                    }
                }
            }
        }
    }

    private fun showSuccessState() {
        // Notification is now handled by the calling fragment to be non-blocking
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
