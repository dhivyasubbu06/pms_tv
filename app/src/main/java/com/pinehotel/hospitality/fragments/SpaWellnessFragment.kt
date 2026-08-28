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
import androidx.navigation.fragment.findNavController
import com.pinehotel.hospitality.R
import com.pinehotel.hospitality.adapters.BookingAdapter
import com.pinehotel.hospitality.adapters.CategoryAdapter
import com.pinehotel.hospitality.databinding.FragmentSpaWellnessBinding
import com.pinehotel.hospitality.models.BookingUIModel
import com.pinehotel.hospitality.viewmodels.BookingViewModel
import com.pinehotel.hospitality.viewmodels.CartViewModel
import com.pinehotel.hospitality.viewmodels.MainViewModel
import com.pinehotel.hospitality.viewmodels.ServicesViewModel
import com.pinehotel.hospitality.utils.NotificationUtils
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SpaWellnessFragment : Fragment() {

    private var _binding: FragmentSpaWellnessBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ServicesViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private val cartViewModel: CartViewModel by activityViewModels()
    private val bookingViewModel: BookingViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSpaWellnessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCategories()
        setupSpaGrid()
        setupCartBar()
        
        viewModel.fetchSpaItems(null)
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

    private fun setupCategories() {
        val categories = listOf("All", "Massage", "Facial", "Body", "Other")
        val adapter = CategoryAdapter(categories) { category ->
            val filter = if (category == "All") null else category
            viewModel.fetchSpaItems(filter)
        }
        binding.rvCategories.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvCategories.adapter = adapter
    }

    private fun setupSpaGrid() {
        viewModel.spaItems.observe(viewLifecycleOwner) { items ->
            binding.rvSpa.layoutManager = GridLayoutManager(context, 3)
            val mappedItems = items.map { 
                BookingUIModel(
                    id = it.id ?: 0,
                    title = it.title ?: "",
                    price = it.price ?: 0.0,
                    imageUrl = it.imageUrl,
                    slots = listOfNotNull(it.slot1, it.slot2, it.slot3),
                    type = "spa"
                )
            }
            binding.rvSpa.adapter = BookingAdapter(mappedItems, cartViewModel) { item, slot ->
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
                        NotificationUtils.showSuccessNotification(binding.root, "Spa reserved successfully")
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
