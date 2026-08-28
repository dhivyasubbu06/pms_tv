package com.pinehotel.hospitality.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pinehotel.hospitality.R
import com.pinehotel.hospitality.databinding.ItemBookingCardBinding
import com.pinehotel.hospitality.models.BookingUIModel
import com.pinehotel.hospitality.utils.FocusUtils
import com.pinehotel.hospitality.utils.PriceUtils
import com.pinehotel.hospitality.utils.UrlUtils
import com.pinehotel.hospitality.viewmodels.CartItem
import com.pinehotel.hospitality.viewmodels.CartViewModel

class BookingAdapter(
    private var items: List<BookingUIModel>,
    private val cartViewModel: CartViewModel,
    private val onBookClick: (BookingUIModel, String) -> Unit
) : RecyclerView.Adapter<BookingAdapter.ViewHolder>() {

    private var expandedPosition = -1

    inner class ViewHolder(private val binding: ItemBookingCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var selectedSlot: String? = null

        fun bind(item: BookingUIModel, position: Int) {
            val isExpanded = position == expandedPosition
            
            // UI state based on expansion
            binding.clCollapsed.visibility = if (isExpanded) View.GONE else View.VISIBLE
            binding.clExpanded.visibility = if (isExpanded) View.VISIBLE else View.GONE

            // Collapsed content
            binding.tvTitle.text = item.title ?: ""
            val price = PriceUtils.getDynamicPrice(item.price ?: 0.0, item.type, item.title)
            binding.tvPrice.text = "₹${String.format("%,.0f", price)}"
            
            val placeholder = when (item.type) {
                "spa" -> R.drawable.ic_spa
                "transport" -> R.drawable.ic_transport
                "activity" -> R.drawable.ic_activity
                "reservation" -> R.drawable.ic_restaurant
                else -> R.drawable.ic_room_service
            }

            val fullImageUrl = UrlUtils.getFullImageUrl(item.imageUrl)
            if (item.type == "transport") {
                // Strictly remove images for transport, show emoji icons as requested
                binding.ivImage.visibility = View.GONE
                binding.tvEmoji.visibility = View.VISIBLE
                binding.tvEmoji.text = when {
                    item.title?.contains("Airport Pickup", ignoreCase = true) == true -> "🚗"
                    item.title?.contains("Airport Drop", ignoreCase = true) == true -> "🚖"
                    item.title?.contains("Taxi", ignoreCase = true) == true -> "🚕"
                    item.title?.contains("Shuttle", ignoreCase = true) == true -> "🚐"
                    else -> "🚗"
                }
            } else {
                binding.ivImage.visibility = View.VISIBLE
                binding.tvEmoji.visibility = View.GONE
                Glide.with(binding.ivImage.context)
                    .load(fullImageUrl)
                    .placeholder(placeholder)
                    .error(placeholder)
                    .into(binding.ivImage)
            }

            // Check if already in cart
            val cartItem = cartViewModel.cartState.value.itemsMap.values.find { 
                it.id == item.id && it.type == item.type 
            }
            
            if (cartItem != null) {
                binding.tvTapToBook.visibility = View.GONE
                binding.tvBookedStatus.visibility = View.VISIBLE
                binding.tvBookedStatus.text = "Booked for ${cartItem.slot} ✓"
            } else {
                binding.tvTapToBook.visibility = View.VISIBLE
                binding.tvBookedStatus.visibility = View.GONE
            }

            // Expanded content (rebuild slots)
            binding.llSlots.removeAllViews()
            item.slots.forEach { slot ->
                val slotView = LayoutInflater.from(binding.root.context)
                    .inflate(R.layout.view_slot_pill, binding.llSlots, false) as TextView
                slotView.text = slot
                slotView.isSelected = (slot == selectedSlot)
                
                slotView.isFocusable = true
                slotView.setOnFocusChangeListener { _, hasFocus ->
                    if (hasFocus) {
                        selectedSlot = slot
                        updateSlotSelection()
                    }
                    FocusUtils.applyScaleAnimation(slotView, hasFocus)
                }
                
                slotView.setOnClickListener {
                    selectedSlot = slot
                    onBookClick(item, slot)
                    toggleExpansion(position)
                }
                
                binding.llSlots.addView(slotView)
            }

            if (selectedSlot == null && item.slots.isNotEmpty()) {
                selectedSlot = item.slots[0]
            }
            updateSlotSelection()

            // Interactions
            binding.clCollapsed.setOnClickListener {
                toggleExpansion(position)
            }

            binding.clCollapsed.setOnFocusChangeListener { _, hasFocus ->
                FocusUtils.applyScaleAnimation(binding.clContainer, hasFocus)
            }

            binding.btnBook.isFocusable = true
            binding.btnBook.setOnFocusChangeListener { view, hasFocus ->
                FocusUtils.applyScaleAnimation(view, hasFocus)
            }

            binding.btnBook.setOnClickListener {
                selectedSlot?.let { slot ->
                    onBookClick(item, slot)
                    toggleExpansion(position)
                }
            }

            // D-pad handling: If expanded, move focus to slots
            if (isExpanded && binding.clCollapsed.hasFocus()) {
                binding.llSlots.getChildAt(0)?.requestFocus()
            }
        }

        private fun toggleExpansion(position: Int) {
            if (expandedPosition == position) {
                expandedPosition = -1
            } else {
                val prev = expandedPosition
                expandedPosition = position
                if (prev != -1) notifyItemChanged(prev)
            }
            notifyItemChanged(position)
        }

        private fun updateSlotSelection() {
            for (i in 0 until binding.llSlots.childCount) {
                val v = binding.llSlots.getChildAt(i) as TextView
                v.isSelected = (v.text == selectedSlot)
            }
            binding.btnBook.text = "BOOK ${selectedSlot ?: ""}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBookingCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], position)
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<BookingUIModel>) {
        this.items = newItems
        notifyDataSetChanged()
    }
}
