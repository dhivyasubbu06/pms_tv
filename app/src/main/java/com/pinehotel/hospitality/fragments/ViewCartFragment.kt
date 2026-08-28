package com.pinehotel.hospitality.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.pinehotel.hospitality.R
import com.pinehotel.hospitality.adapters.CartAdapter
import com.pinehotel.hospitality.databinding.FragmentViewCartBinding
import com.pinehotel.hospitality.viewmodels.BookingViewModel
import com.pinehotel.hospitality.viewmodels.CartViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ViewCartFragment : Fragment() {

    private var _binding: FragmentViewCartBinding? = null
    private val binding get() = _binding!!
    private val cartViewModel: CartViewModel by activityViewModels()
    private val bookingViewModel: BookingViewModel by activityViewModels()
    private lateinit var adapter: CartAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentViewCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = CartAdapter { item, delta ->
            cartViewModel.updateQuantity(item, delta)
        }
        binding.rvCartItems.layoutManager = LinearLayoutManager(context)
        binding.rvCartItems.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            cartViewModel.cartState.collectLatest { state ->
                adapter.submitList(state.itemsMap.values.toList())
                binding.tvGrandTotal.text = "₹${String.format("%.2f", state.totalPrice)}"
                
                if (state.totalCount == 0) {
                    Toast.makeText(context, "Cart is empty", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            cartViewModel.isSubmitting.collectLatest { isSubmitting ->
                binding.progressBar.visibility = if (isSubmitting) View.VISIBLE else View.GONE
                binding.btnPlaceOrder.isEnabled = !isSubmitting
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            cartViewModel.submissionState.collectLatest { results ->
                if (results.isNotEmpty()) {
                    if (results.all { it.success }) {
                        bookingViewModel.fetchBookings()
                        findNavController().navigate(R.id.action_viewCart_to_orderConfirmation)
                    } else {
                        val failedResults = results.filter { !it.success }
                        val errorMsg = failedResults.joinToString("\n") { "${it.serviceType}: ${it.message}" }
                        Toast.makeText(context, "Order failed:\n$errorMsg", Toast.LENGTH_LONG).show()
                        cartViewModel.clearSubmissionState()
                    }
                }
            }
        }

        binding.btnPlaceOrder.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                val roomNo = cartViewModel.getRoomNumber()
                if (roomNo == null || roomNo == "Unknown") {
                    Toast.makeText(context, "Room number not set. Please check settings.", Toast.LENGTH_LONG).show()
                } else {
                    cartViewModel.placeOrder()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
