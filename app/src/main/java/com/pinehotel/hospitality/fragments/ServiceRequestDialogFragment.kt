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
import com.pinehotel.hospitality.databinding.DialogServiceRequestBinding
import com.pinehotel.hospitality.utils.FocusUtils
import com.pinehotel.hospitality.utils.PreferenceManager
import com.pinehotel.hospitality.utils.UrlUtils
import com.pinehotel.hospitality.viewmodels.MainViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ServiceRequestDialogFragment : DialogFragment() {

    private var _binding: DialogServiceRequestBinding? = null
    private val binding get() = _binding!!
    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var preferenceManager: PreferenceManager

    private var serviceId: Int = -1
    private var serviceTitle: String = ""
    private var serviceDesc: String = ""
    private var imageUrl: String? = null
    private var serviceType: String = ""

    companion object {
        private const val ARG_ID = "service_id"
        private const val ARG_TITLE = "service_title"
        private const val ARG_DESC = "service_desc"
        private const val ARG_IMAGE = "image_url"
        private const val ARG_TYPE = "service_type"

        fun newInstance(id: Int, title: String, desc: String, image: String?, type: String): ServiceRequestDialogFragment {
            val fragment = ServiceRequestDialogFragment()
            val args = Bundle()
            args.putInt(ARG_ID, id)
            args.putString(ARG_TITLE, title)
            args.putString(ARG_DESC, desc)
            args.putString(ARG_IMAGE, image)
            args.putString(ARG_TYPE, type)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            serviceId = it.getInt(ARG_ID)
            serviceTitle = it.getString(ARG_TITLE) ?: ""
            serviceDesc = it.getString(ARG_DESC) ?: ""
            imageUrl = it.getString(ARG_IMAGE)
            serviceType = it.getString(ARG_TYPE) ?: "maintenance"
        }
        preferenceManager = PreferenceManager(requireContext())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        _binding = DialogServiceRequestBinding.inflate(inflater, container, false)
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
        binding.tvServiceLabel.text = serviceTitle
        binding.tvServiceDesc.text = serviceDesc

        lifecycleScope.launch {
            val room = preferenceManager.roomNumberFlow.first() ?: "---"
            binding.tvRoomLabel.text = "Room $room"
        }

        val fullImageUrl = UrlUtils.getFullImageUrl(imageUrl)
        if (!fullImageUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(fullImageUrl)
                .placeholder(R.drawable.ic_housekeeping)
                .into(binding.ivServiceIcon)
        }

        // TV Focus highlights for inputs and buttons
        val focusableViews = listOf(binding.btnClose, binding.etNote, binding.btnCancel, binding.btnSend)
        focusableViews.forEach { v ->
            v.isFocusable = true
            v.setOnFocusChangeListener { view, hasFocus ->
                FocusUtils.applyScaleAnimation(view, hasFocus)
            }
        }

        binding.btnClose.setOnClickListener { dismiss() }
        binding.btnCancel.setOnClickListener { dismiss() }

        binding.btnSend.setOnClickListener {
            val note = binding.etNote.text.toString().trim()
            mainViewModel.submitServiceRequest(serviceType, serviceId, serviceTitle, note)
            dismiss()
        }

        // Auto focus Send button so D-Pad remote can interact immediately
        binding.btnSend.post {
            binding.btnSend.requestFocus()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
