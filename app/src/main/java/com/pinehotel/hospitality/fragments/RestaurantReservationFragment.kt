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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.pinehotel.hospitality.adapters.BookingAdapter
import com.pinehotel.hospitality.adapters.CategoryAdapter
import com.pinehotel.hospitality.databinding.FragmentRestaurantReservationBinding
import com.pinehotel.hospitality.models.BookingUIModel
import com.pinehotel.hospitality.viewmodels.BookingViewModel
import com.pinehotel.hospitality.viewmodels.CartViewModel
import com.pinehotel.hospitality.utils.NotificationUtils
import com.pinehotel.hospitality.viewmodels.MainViewModel
import com.pinehotel.hospitality.viewmodels.ServicesViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RestaurantReservationFragment : Fragment() {

    private var _binding: FragmentRestaurantReservationBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ServicesViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private val cartViewModel: CartViewModel by activityViewModels()
    private val bookingViewModel: BookingViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRestaurantReservationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCategories()
        setupRestaurantGrid()
        
        viewModel.fetchDineItems(null)
        observeSubmission()
    }

    private fun setupCategories() {
        val categories = listOf("All", "Romantic", "Birthday", "Anniversary", "Business", "Family")
        val adapter = CategoryAdapter(categories) { occasion ->
            val filter = if (occasion == "All") null else occasion
            viewModel.fetchDineItems(filter)
        }
        binding.rvCategories.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvCategories.adapter = adapter
    }

    private fun setupRestaurantGrid() {
        viewModel.dineItems.observe(viewLifecycleOwner) { items ->
            binding.rvRestaurants.layoutManager = GridLayoutManager(context, 3)
            val mappedItems = items.map { 
                BookingUIModel(
                    id = it.id ?: 0,
                    title = it.title ?: "",
                    price = it.price ?: 0.0,
                    imageUrl = it.imageUrl,
                    slots = listOfNotNull(it.slot1, it.slot2, it.slot3),
                    type = "reservation"
                )
            }
            binding.rvRestaurants.adapter = BookingAdapter(mappedItems, cartViewModel) { item, slot ->
                val dialog = BookingConfirmationDialogFragment.newInstance(
                    item.id, item.title, item.price, item.type, slot, item.imageUrl
                )
                dialog.show(childFragmentManager, "booking_confirm")
            }
        }
    }

    private fun observeSubmission() {
        viewLifecycleOwner.lifecycleScope.launch {
            mainViewModel.requestSuccess.collectLatest { success ->
                success?.let {
                    if (it) {
                        NotificationUtils.showSuccessNotification(binding.root, "Reservation confirmed successfully")
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
