package com.pinehotel.hospitality.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.pinehotel.hospitality.adapters.HousekeepingAdapter
import com.pinehotel.hospitality.databinding.FragmentHousekeepingBinding
import com.pinehotel.hospitality.viewmodels.BookingViewModel
import com.pinehotel.hospitality.viewmodels.MainViewModel
import com.pinehotel.hospitality.viewmodels.ServicesViewModel
import com.pinehotel.hospitality.utils.NotificationUtils
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HousekeepingFragment : Fragment() {

    private var _binding: FragmentHousekeepingBinding? = null
    private val binding get() = _binding!!
    private val servicesViewModel: ServicesViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private val bookingViewModel: BookingViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHousekeepingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainViewModel.serviceItems.collectLatestInLifecycle { items ->
            binding.rvHousekeeping.layoutManager = GridLayoutManager(context, 2)
            binding.rvHousekeeping.adapter = HousekeepingAdapter(items) { service ->
                val dialog = ServiceRequestDialogFragment.newInstance(
                    service.id,
                    service.title,
                    service.description ?: "",
                    service.imageUrl,
                    "housekeeping" // Pass the module type
                )
                dialog.show(childFragmentManager, "service_request")
            }
        }

        mainViewModel.fetchRoomServiceItems()
        observeViewModel()
    }

    private fun <T> kotlinx.coroutines.flow.Flow<T>.collectLatestInLifecycle(action: suspend (T) -> Unit) {
        viewLifecycleOwner.lifecycleScope.launch {
            this@collectLatestInLifecycle.collectLatest(action)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            mainViewModel.requestSuccess.collectLatest { success ->
                success?.let {
                    if (it) {
                        NotificationUtils.showSuccessNotification(binding.root, "Housekeeping request sent successfully")
                        bookingViewModel.fetchBookings()
                    }
                    mainViewModel.resetRequestState()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            mainViewModel.error.collectLatest { error ->
                error?.let {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
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