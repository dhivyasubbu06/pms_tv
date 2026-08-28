package com.pinehotel.hospitality.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.pinehotel.hospitality.R
import com.pinehotel.hospitality.databinding.FragmentMyRequestsBinding
import com.pinehotel.hospitality.utils.PriceUtils
import com.pinehotel.hospitality.viewmodels.BookingViewModel
import com.pinehotel.hospitality.viewmodels.CartViewModel
import com.pinehotel.hospitality.viewmodels.MainViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MyRequestsFragment : Fragment() {

    private var _binding: FragmentMyRequestsBinding? = null
    private val binding get() = _binding!!
    private val mainViewModel: MainViewModel by activityViewModels()
    private val bookingViewModel: BookingViewModel by viewModels()
    private val cartViewModel: CartViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyRequestsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel()
        setupCartBar()
        
        binding.btnClose.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnChangePlan.setOnClickListener {
            // Future meal plan change logic
        }

        bookingViewModel.fetchBookings()
        
        binding.btnClose.requestFocus()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            bookingViewModel.bookingData.collectLatest { data ->
                data?.let { response ->
                    // Separate categories from backend arrays (Primary source for commerce items)
                    val foodItems = ((response.foodOrders ?: emptyList()) + response.orders.filter { 
                        val type = (it.orderType ?: it.serviceType ?: it.type ?: "").lowercase()
                        type.contains("food") || type.contains("dining") || type.contains("room service") || type.contains("restaurant")
                    }).distinctBy { "${it.id}_${it.title}_${it.orderedAt}" }
                    
                    val barItems = ((response.barOrders ?: emptyList()) + response.orders.filter { 
                        val type = (it.orderType ?: it.serviceType ?: it.type ?: "").lowercase()
                        type.contains("bar") || type.contains("lounge") || type.contains("drink")
                    }).distinctBy { "${it.id}_${it.title}_${it.orderedAt}" }
                    
                    // Manual Total Calculation (matching website logic: Sum all items)
                    val allItems = (foodItems + barItems + (response.spaBookings ?: emptyList()) + 
                                   (response.entertainmentBookings ?: emptyList()) + (response.dineBookings ?: emptyList()) + 
                                   (response.activityBookings ?: emptyList()))
                    
                    val manualGrand = allItems.sumOf { calculateOrderTotal(it) }
                    binding.tvGrandTotal.text = "₹$manualGrand"

                    val breakdown = mutableListOf<String>()
                    val fSum = foodItems.sumOf { calculateOrderTotal(it) }
                    val bSum = barItems.sumOf { calculateOrderTotal(it) }
                    val sSum = (response.spaBookings ?: emptyList()).sumOf { calculateOrderTotal(it) }
                    val dSum = (response.dineBookings ?: emptyList()).sumOf { calculateOrderTotal(it) }

                    if (fSum > 0) breakdown.add("Food ₹$fSum")
                    if (bSum > 0) breakdown.add("Bar ₹$bSum")
                    if (sSum > 0) breakdown.add("Spa ₹$sSum")
                    if (dSum > 0) breakdown.add("Dine ₹$dSum")

                    // Pending info line
                    val pendingAmount = allItems.filter { getActualStatus(it) == "pending" }
                                               .sumOf { calculateOrderTotal(it) }
                    
                    var subtitle = breakdown.joinToString(" · ")
                    if (pendingAmount > 0) {
                        subtitle += if (subtitle.isNotEmpty()) "\n" else ""
                        subtitle += "+ ₹$pendingAmount pending confirmation"
                    }
                    
                    binding.tvChargesSub.text = if (subtitle.isNotEmpty()) subtitle else "No charges yet"
                    binding.tvMealPlanName.text = response.mealPlan ?: "Room Only"
                    
                    populateList(binding.llFoodItems, binding.tvFoodEmpty, foodItems)
                    populateList(binding.llBarItems, binding.tvBarEmpty, barItems)
                    populateList(binding.llSpaItems, binding.tvSpaEmpty, response.spaBookings ?: emptyList())
                    populateList(binding.llEntertainmentItems, binding.tvEntertainmentEmpty, response.entertainmentBookings ?: emptyList())
                    populateList(binding.llDiningItems, binding.tvDiningEmpty, response.dineBookings ?: emptyList())
                    populateList(binding.llActivityItems, binding.tvActivityEmpty, response.activityBookings ?: emptyList())
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            bookingViewModel.isLoading.collectLatest { isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }
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

    private fun populateList(container: LinearLayout, emptyText: TextView, items: List<com.pinehotel.hospitality.network.GuestOrder>) {
        container.removeAllViews()
        if (items.isEmpty()) {
            emptyText.visibility = View.VISIBLE
        } else {
            emptyText.visibility = View.GONE
            items.forEach { item ->
                val itemView = LayoutInflater.from(context).inflate(R.layout.item_booking_entry, container, false)
                
                // Parse Title from 'items' if missing or generic
                var displayTitle = item.title ?: item.details ?: "Order"
                if ((displayTitle == "Order" || displayTitle.isEmpty() || displayTitle.startsWith("[")) && !item.items.isNullOrEmpty()) {
                    try {
                        // Match: 'name': 'Ven Pongal', 'qty': 1
                        val itemRegex = "'name':\\s*'([^']+)',\\s*'qty':\\s*(\\d+)".toRegex()
                        val matches = itemRegex.findAll(item.items)
                        val extracted = matches.map { it.groupValues[1] + " x" + it.groupValues[2] }.toList()
                        if (extracted.isNotEmpty()) {
                            displayTitle = extracted.joinToString(", ")
                        }
                    } catch (e: Exception) {}
                }
                itemView.findViewById<TextView>(R.id.tvItemTitle).text = displayTitle
                
                // Show Price + Time for commerce orders (as in screenshot)
                val type = item.orderType ?: item.serviceType ?: item.type ?: ""
                val price = PriceUtils.getDynamicPriceInt(item.total ?: item.amount ?: item.price ?: 0, type, displayTitle)
                val time = item.slot ?: item.orderedAt ?: item.bookedAt ?: ""
                itemView.findViewById<TextView>(R.id.tvItemTime).text = "₹$price · $time"
                
                val statusView = itemView.findViewById<TextView>(R.id.tvItemStatus)
                val actualStatus = getActualStatus(item)
                statusView.text = actualStatus.uppercase()
                
                val color = when(actualStatus.lowercase()) {
                    "confirmed", "delivered", "completed", "accepted" -> android.graphics.Color.parseColor("#4CAF50")
                    "pending" -> android.graphics.Color.parseColor("#FFC107")
                    "cancelled" -> android.graphics.Color.parseColor("#E53935")
                    else -> android.graphics.Color.parseColor("#9C8878")
                }
                statusView.setTextColor(color)
                container.addView(itemView)
            }
        }
    }

    private fun getActualStatus(order: com.pinehotel.hospitality.network.GuestOrder): String {
        val status = (order.status ?: "pending").lowercase()
        if (status == "confirmed" || status == "delivered" || status == "completed" || status == "accepted") {
            return "confirmed"
        }
        if (status == "cancelled") return "cancelled"
        
        // Logic: Auto-confirm items older than 10 minutes
        try {
            val timeStr = order.orderedAt ?: order.bookedAt ?: return status
            val format = java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a", java.util.Locale.getDefault())
            val orderDate = format.parse(timeStr)
            if (orderDate != null) {
                val diff = System.currentTimeMillis() - orderDate.time
                if (diff > 10 * 60 * 1000) {
                    return "confirmed"
                }
            }
        } catch (e: Exception) {}
        return status
    }

    private fun calculateOrderTotal(item: com.pinehotel.hospitality.network.GuestOrder): Int {
        val type = item.orderType ?: item.serviceType ?: item.type ?: ""
        val title = item.title ?: item.details ?: ""
        return PriceUtils.getDynamicPriceInt(item.total ?: item.price ?: item.amount ?: 0, type, title)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
