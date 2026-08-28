package com.pinehotel.hospitality.ui

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.GridLayoutManager
import com.pinehotel.hospitality.R
import com.pinehotel.hospitality.adapters.CategoryData
import com.pinehotel.hospitality.adapters.CategoryPanelAdapter
import com.pinehotel.hospitality.databinding.ActivityBookingBinding
import com.pinehotel.hospitality.network.MyOrdersResponse
import com.pinehotel.hospitality.utils.PriceUtils
import com.pinehotel.hospitality.viewmodels.BookingViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class BookingActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityBookingBinding
    private val viewModel: BookingViewModel by viewModels()
    private val handler = android.os.Handler(android.os.Looper.getMainLooper())
    private val refreshRunnable = object : Runnable {
        override fun run() {
            viewModel.fetchBookings()
            handler.postDelayed(this, 30000) // Refresh every 30 seconds
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnClose.setOnClickListener { finish() }
        
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        binding.rvCategoryGrid.layoutManager = GridLayoutManager(this, 3)
        // Initialize with empty data so panels show "No bookings yet" immediately
        updateCategories(null)
    }

    override fun onResume() {
        super.onResume()
        handler.post(refreshRunnable)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(refreshRunnable)
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.bookingData.collectLatest { data ->
                updateUI(data)
            }
        }

        lifecycleScope.launch {
            viewModel.isLoading.collectLatest { isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }

        lifecycleScope.launch {
            viewModel.error.collectLatest { error ->
                error?.let {
                    android.widget.Toast.makeText(this@BookingActivity, it, android.widget.Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateUI(data: MyOrdersResponse?) {
        data?.let { response ->
            // Manual Total Calculation to ensure "instant" updates matching website
            val foodItems = ((response.foodOrders ?: emptyList()) + response.orders.filter { 
                val type = (it.orderType ?: it.serviceType ?: it.type ?: "").lowercase()
                type.contains("food") || type.contains("dining") || type.contains("room service") || type.contains("restaurant")
            }).distinctBy { "${it.id}_${it.title}_${it.orderedAt}" }

            val barItems = ((response.barOrders ?: emptyList()) + response.orders.filter { 
                val type = (it.orderType ?: it.serviceType ?: it.type ?: "").lowercase()
                type.contains("bar") || type.contains("lounge") || type.contains("drink")
            }).distinctBy { "${it.id}_${it.title}_${it.orderedAt}" }

            val allItems = (foodItems + barItems + (response.spaBookings ?: emptyList()) + 
                           (response.entertainmentBookings ?: emptyList()) + (response.dineBookings ?: emptyList()) + 
                           (response.activityBookings ?: emptyList()))
            
            val manualGrand = allItems.sumOf { 
                val type = it.orderType ?: it.serviceType ?: it.type ?: ""
                val title = it.title ?: it.details ?: ""
                PriceUtils.getDynamicPriceInt(it.total ?: it.amount ?: it.price ?: 0, type, title)
            }
            binding.tvGrandTotal.text = "₹$manualGrand"

            val breakdown = mutableListOf<String>()
            val fSum = foodItems.sumOf { 
                val type = it.orderType ?: it.serviceType ?: it.type ?: ""
                val title = it.title ?: it.details ?: ""
                PriceUtils.getDynamicPriceInt(it.total ?: it.amount ?: it.price ?: 0, type, title)
            }
            val bSum = barItems.sumOf { 
                val type = it.orderType ?: it.serviceType ?: it.type ?: ""
                val title = it.title ?: it.details ?: ""
                PriceUtils.getDynamicPriceInt(it.total ?: it.amount ?: it.price ?: 0, type, title)
            }
            val sSum = (response.spaBookings ?: emptyList()).sumOf { 
                val type = it.orderType ?: it.serviceType ?: it.type ?: ""
                val title = it.title ?: it.details ?: ""
                PriceUtils.getDynamicPriceInt(it.total ?: it.amount ?: it.price ?: 0, type, title)
            }
            val dSum = (response.dineBookings ?: emptyList()).sumOf { 
                val type = it.orderType ?: it.serviceType ?: it.type ?: ""
                val title = it.title ?: it.details ?: ""
                PriceUtils.getDynamicPriceInt(it.total ?: it.amount ?: it.price ?: 0, type, title)
            }

            if (fSum > 0) breakdown.add("Food ₹$fSum")
            if (bSum > 0) breakdown.add("Bar ₹$bSum")
            if (sSum > 0) breakdown.add("Spa ₹$sSum")
            if (dSum > 0) breakdown.add("Dine ₹$dSum")

            binding.tvTotalBreakdown.text = if (breakdown.isEmpty()) "No charges yet" else breakdown.joinToString(" · ")
            binding.tvMealPlanValue.text = response.mealPlan ?: "Room Only"
        }
        updateCategories(data)
    }

    private fun updateCategories(data: MyOrdersResponse?) {
        android.util.Log.d("BookingActivity", "Updating categories. Data is null: ${data == null}")
        
        val foodItems = data?.let { response ->
            ((response.foodOrders ?: emptyList()) + response.orders.filter { 
                val type = (it.orderType ?: it.serviceType ?: it.type ?: "").lowercase()
                type.contains("food") || type.contains("dining") || type.contains("room service") || type.contains("restaurant")
            }).distinctBy { "${it.id}_${it.title}_${it.orderedAt}" }
        } ?: emptyList()

        val barItems = data?.let { response ->
            ((response.barOrders ?: emptyList()) + response.orders.filter { 
                val type = (it.orderType ?: it.serviceType ?: it.type ?: "").lowercase()
                type.contains("bar") || type.contains("lounge") || type.contains("drink")
            }).distinctBy { "${it.id}_${it.title}_${it.orderedAt}" }
        } ?: emptyList()

        val categories = listOf(
            CategoryData("Food Orders", "🍴", foodItems),
            CategoryData("Spa & Wellness", "💆", data?.spaBookings ?: emptyList()),
            CategoryData("Bar & Lounge", "🍸", barItems),
            CategoryData("Entertainment", "🎭", data?.entertainmentBookings ?: emptyList()),
            CategoryData("Dining Reservations", "🍗", data?.dineBookings ?: emptyList()),
            CategoryData("Activity Reservations", "🎯", data?.activityBookings ?: emptyList())
        )
        
        val adapter = CategoryPanelAdapter(categories)
        binding.rvCategoryGrid.adapter = adapter
        
        // Ensure visibility and request layout
        binding.rvCategoryGrid.visibility = View.VISIBLE
        binding.rvCategoryGrid.post {
            binding.rvCategoryGrid.requestLayout()
        }
    }
}
