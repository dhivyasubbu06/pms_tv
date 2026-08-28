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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.pinehotel.hospitality.R
import com.pinehotel.hospitality.adapters.BookingAdapter
import com.pinehotel.hospitality.adapters.CategoryAdapter
import com.pinehotel.hospitality.databinding.FragmentEntertainmentBinding
import com.pinehotel.hospitality.models.BookingUIModel
import com.pinehotel.hospitality.viewmodels.BookingViewModel
import com.pinehotel.hospitality.viewmodels.CartViewModel
import com.pinehotel.hospitality.utils.NotificationUtils
import com.pinehotel.hospitality.viewmodels.MainViewModel
import com.pinehotel.hospitality.viewmodels.ServicesViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class EntertainmentFragment : Fragment() {

    private var _binding: FragmentEntertainmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ServicesViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private val cartViewModel: CartViewModel by activityViewModels()
    private val bookingViewModel: BookingViewModel by activityViewModels()

    private var currentCategory: String? = null
    private var bookingAdapter: BookingAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEntertainmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val handler = android.os.Handler(android.os.Looper.getMainLooper())
    private val refreshRunnable = object : Runnable {
        override fun run() {
            viewModel.fetchEntertainmentItems(currentCategory, silent = true)
            handler.postDelayed(this, 10000)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCategories()
        setupEntertainmentGrid()
        setupCartBar()
        
        viewModel.fetchEntertainmentItems(null)
        observeSubmission()
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

    private fun setupCategories() {
        // Categories matching your portal: Indoor, Outdoor, Night, etc.
        val categories = listOf("All", "Indoor", "Outdoor", "Night", "Water", "Kids")
        val adapter = CategoryAdapter(categories) { category ->
            currentCategory = if (category == "All") null else category
            viewModel.fetchEntertainmentItems(currentCategory)
        }
        binding.rvCategories.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvCategories.adapter = adapter
    }

    private fun setupEntertainmentGrid() {
        binding.rvEntertainment.layoutManager = GridLayoutManager(context, 3)
        
        viewModel.entertainmentItems.observe(viewLifecycleOwner) { items ->
            val mappedItems = items.map { 
                BookingUIModel(
                    id = it.id ?: 0,
                    title = it.title ?: "",
                    price = it.price ?: 0.0,
                    imageUrl = it.imageUrl,
                    slots = listOfNotNull(it.slot1, it.slot2, it.slot3),
                    type = "entertainment"
                )
            }
            
            if (bookingAdapter == null) {
                bookingAdapter = BookingAdapter(mappedItems, cartViewModel) { item, slot ->
                    val dialog = BookingConfirmationDialogFragment.newInstance(
                        item.id, item.title, item.price, item.type, slot, item.imageUrl
                    )
                    dialog.show(childFragmentManager, "booking_confirm")
                }
                binding.rvEntertainment.adapter = bookingAdapter
            } else {
                bookingAdapter?.updateItems(mappedItems)
            }
            
            // Request focus on the first item if no item is focused
            if (binding.rvEntertainment.findFocus() == null) {
                binding.rvEntertainment.post {
                    binding.rvEntertainment.findViewHolderForAdapterPosition(0)?.itemView?.requestFocus()
                }
            }
        }
    }

    private fun observeSubmission() {
        viewLifecycleOwner.lifecycleScope.launch {
            mainViewModel.requestSuccess.collectLatest { success ->
                success?.let {
                    if (it) {
                        NotificationUtils.showSuccessNotification(binding.root, "Entertainment booking successful")
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
