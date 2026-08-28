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
import com.pinehotel.hospitality.adapters.CategoryAdapter
import com.pinehotel.hospitality.adapters.FoodAdapter
import com.pinehotel.hospitality.databinding.FragmentRoomServiceBinding
import com.pinehotel.hospitality.viewmodels.CartViewModel
import com.pinehotel.hospitality.viewmodels.MainViewModel
import com.pinehotel.hospitality.viewmodels.ServicesViewModel
import com.pinehotel.hospitality.utils.NotificationUtils
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class BarFragment : Fragment() {

    private var _binding: FragmentRoomServiceBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ServicesViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private val cartViewModel: CartViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Reusing room service layout for drinks grid
        _binding = FragmentRoomServiceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.tvTitle.text = "LOUNGE & BAR"

        setupCategories()
        setupDrinkGrid()
        setupCartBar()
        
        viewModel.fetchBarItems(null)
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
        val categories = listOf("All", "Alcoholic", "Non-Alcoholic")
        val adapter = CategoryAdapter(categories) { category ->
            val filter = if (category == "All") null else category
            viewModel.fetchBarItems(filter)
        }
        binding.rvCategories.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvCategories.adapter = adapter
    }

    private fun setupDrinkGrid() {
        viewModel.barItems.observe(viewLifecycleOwner) { items ->
            binding.rvFood.layoutManager = GridLayoutManager(context, 5)
            val mappedItems = items.map { 
                com.pinehotel.hospitality.network.FoodMenuItem(
                    id = it.id ?: 0,
                    title = it.title ?: "",
                    description = it.description ?: "",
                    price = it.price ?: 0.0,
                    imageUrl = it.imageUrl,
                    category = null
                )
            }
            binding.rvFood.adapter = FoodAdapter(mappedItems, cartViewModel, "bar")
        }
    }

    private fun observeSubmission() {
        viewLifecycleOwner.lifecycleScope.launch {
            mainViewModel.requestSuccess.collectLatest { success ->
                success?.let {
                    if (it) {
                        NotificationUtils.showSuccessNotification(binding.root, "Bar order placed successfully")
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
