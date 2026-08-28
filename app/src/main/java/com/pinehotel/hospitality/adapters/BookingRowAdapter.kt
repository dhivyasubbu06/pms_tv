package com.pinehotel.hospitality.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.pinehotel.hospitality.R
import com.pinehotel.hospitality.databinding.ItemBookingRowBinding
import com.pinehotel.hospitality.network.GuestOrder
import com.pinehotel.hospitality.utils.PriceUtils

class BookingRowAdapter(private val bookings: List<GuestOrder>) :
    RecyclerView.Adapter<BookingRowAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemBookingRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(order: GuestOrder) {
            // 1. Determine Title (Service Name)
            var displayTitle = order.title ?: order.details ?: "Order"
            
            // If title is generic, try to extract from 'items' string (JSON-like array from backend)
            if ((displayTitle == "Order" || displayTitle.isEmpty() || displayTitle.startsWith("[")) && !order.items.isNullOrEmpty()) {
                try {
                    // Match: 'name': 'Ven Pongal', 'qty': 1
                    val itemRegex = "'name':\\s*'([^']+)',\\s*'qty':\\s*(\\d+)".toRegex()
                    val matches = itemRegex.findAll(order.items)
                    val extracted = matches.map { it.groupValues[1] + " x" + it.groupValues[2] }.toList()
                    if (extracted.isNotEmpty()) {
                        displayTitle = extracted.joinToString(", ")
                    }
                } catch (e: Exception) {}
            }
            binding.tvServiceName.text = displayTitle
            
            // 2. Status Pill (with auto-confirm logic for 10+ mins)
            val actualStatus = getActualStatus(order)
            binding.tvStatus.text = actualStatus.uppercase()
            
            if (actualStatus.lowercase() == "confirmed" || actualStatus.lowercase() == "delivered") {
                binding.tvStatus.setBackgroundResource(R.drawable.bg_status_teal_outline)
                binding.tvStatus.setTextColor(android.graphics.Color.parseColor("#26A69A"))
            } else if (actualStatus.lowercase() == "cancelled") {
                binding.tvStatus.setBackgroundResource(R.drawable.bg_slot_pill)
                binding.tvStatus.setTextColor(android.graphics.Color.parseColor("#E53935"))
            } else {
                binding.tvStatus.setBackgroundResource(R.drawable.bg_slot_pill)
                binding.tvStatus.setTextColor(ContextCompat.getColor(binding.root.context, R.color.status_pending))
            }
            
            // 3. Subtitle (Price · [Slot] · Timestamp)
            val type = order.orderType ?: order.serviceType ?: order.type ?: ""
            val cost = PriceUtils.getDynamicPriceInt(order.total ?: order.amount ?: order.price ?: 0, type, displayTitle)
            val costStr = "₹${String.format("%,d", cost)}"
            val timestamp = order.orderedAt ?: order.bookedAt ?: ""
            
            val infoList = mutableListOf<String>()
            infoList.add(costStr)
            if (!order.slot.isNullOrEmpty()) infoList.add(order.slot)
            if (timestamp.isNotEmpty()) infoList.add(timestamp)
            
            binding.tvDetails.text = infoList.joinToString(" · ")
        }

        private fun getActualStatus(order: GuestOrder): String {
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
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBookingRowBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(bookings[position])
    }

    override fun getItemCount(): Int = bookings.size
}
