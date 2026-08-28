package com.pinehotel.hospitality.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.pinehotel.hospitality.R
import androidx.recyclerview.widget.LinearLayoutManager
import com.pinehotel.hospitality.adapters.ActivityScheduleAdapter
import com.pinehotel.hospitality.databinding.FragmentActivityBookingBinding
import com.pinehotel.hospitality.network.Activity
import com.pinehotel.hospitality.utils.NotificationUtils
import com.pinehotel.hospitality.viewmodels.BookingViewModel
import com.pinehotel.hospitality.viewmodels.CartViewModel
import com.pinehotel.hospitality.viewmodels.MainViewModel
import com.pinehotel.hospitality.viewmodels.ServicesViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ActivityBookingFragment : Fragment() {

    private var _binding: FragmentActivityBookingBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ServicesViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private val cartViewModel: CartViewModel by activityViewModels()
    private val bookingViewModel: BookingViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentActivityBookingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvTitle.text = "Today's schedule"
        binding.rvCategories.visibility = View.GONE 
        
        setupActivityList()
        setupCartBar()
        
        viewModel.fetchActivities()
        observeSubmission()
    }

    private fun setupCartBar() {
        viewLifecycleOwner.lifecycleScope.launch {
            cartViewModel.cartState.collect { state ->
                if (state.totalCount > 0) {
                    binding.layoutCartBar.clCartBar.visibility = View.VISIBLE
                    binding.layoutCartBar.tvCartSummary.text = 
                        "${state.totalCount} items · ₹${String.format("%.2f", state.totalPrice)}"
                } else {
                    binding.layoutCartBar.clCartBar.visibility = View.GONE
                }
            }
        }
        
        binding.layoutCartBar.btnViewCart.setOnClickListener {
            findNavController().navigate(R.id.action_global_viewCartFragment)
        }
    }

    private fun setupActivityList() {
        viewModel.activities.observe(viewLifecycleOwner) { items ->
            binding.rvActivities.layoutManager = LinearLayoutManager(context)
            binding.rvActivities.itemAnimator = null
            
            val adapter = ActivityScheduleAdapter(items) { activity ->
                // Handle click to book
                mainViewModel.submitServiceRequest(
                    type = "activity",
                    serviceId = activity.id,
                    serviceTitle = activity.title,
                    note = activity.timeSlot
                )
            }
            binding.rvActivities.adapter = adapter
            
            // Request focus on first item
            binding.rvActivities.post {
                binding.rvActivities.findViewHolderForAdapterPosition(0)?.itemView?.requestFocus()
            }
        }
    }

    private fun observeSubmission() {
        viewLifecycleOwner.lifecycleScope.launch {
            mainViewModel.requestSuccess.collectLatest { success ->
                success?.let {
                    if (it) {
                        val currentActivity = viewModel.activities.value?.firstOrNull() 
                        val title = currentActivity?.title ?: "Activity"
                        NotificationUtils.showSuccessNotification(binding.root, "$title reserved successfully")
                        bookingViewModel.fetchBookings()
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
