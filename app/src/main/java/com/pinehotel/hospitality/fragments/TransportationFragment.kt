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
import com.pinehotel.hospitality.databinding.FragmentTransportationBinding
import com.pinehotel.hospitality.models.BookingUIModel
import com.pinehotel.hospitality.viewmodels.BookingViewModel
import com.pinehotel.hospitality.viewmodels.CartViewModel
import com.pinehotel.hospitality.viewmodels.MainViewModel
import com.pinehotel.hospitality.viewmodels.ServicesViewModel
import com.pinehotel.hospitality.utils.NotificationUtils
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class TransportationFragment : Fragment() {

    private var _binding: FragmentTransportationBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ServicesViewModel by viewModels()
    private val cartViewModel: CartViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransportationBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val handler = android.os.Handler(android.os.Looper.getMainLooper())
    private val refreshRunnable = object : Runnable {
        override fun run() {
            viewModel.fetchTransportItems(null, silent = true)
            handler.postDelayed(this, 10000)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCategories()
        setupTransportGrid()
        setupCartBar()
        
        viewModel.fetchTransportItems()
    }

    private fun setupCategories() {
        val categories = listOf("All", "Airport", "Local", "Outstation")
        val adapter = CategoryAdapter(categories) { category ->
            viewModel.fetchTransportItems(category)
        }
        binding.rvCategories.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvCategories.adapter = adapter
    }

    private fun setupTransportGrid() {
        viewModel.transportItems.observe(viewLifecycleOwner) { items ->
            binding.rvTransport.layoutManager = GridLayoutManager(context, 3)
            val mappedItems = items.map { 
                BookingUIModel(
                    id = it.id ?: 0,
                    title = it.title ?: "",
                    price = it.price ?: 0.0,
                    imageUrl = it.imageUrl,
                    slots = listOf("ASAP", "In 30 mins", "In 1 hour", "Evening"),
                    type = "transport"
                )
            }
            binding.rvTransport.adapter = BookingAdapter(mappedItems, cartViewModel) { item, slot ->
                val dialog = TransportBookingDialogFragment.newInstance(
                    item.id ?: 0, item.title ?: "Transport", slot
                )
                dialog.show(childFragmentManager, "transport_details")
            }

            // Request focus on the first item for D-pad navigation
            binding.rvTransport.post {
                binding.rvTransport.findViewHolderForAdapterPosition(0)?.itemView?.requestFocus()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        handler.post(refreshRunnable)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(refreshRunnable)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
