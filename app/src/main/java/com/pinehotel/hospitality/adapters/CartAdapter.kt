package com.pinehotel.hospitality.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.pinehotel.hospitality.R
import com.pinehotel.hospitality.databinding.ItemCartHeaderBinding
import com.pinehotel.hospitality.databinding.ItemCartLineBinding
import com.pinehotel.hospitality.utils.FocusUtils
import com.pinehotel.hospitality.viewmodels.CartItem

class CartAdapter(
    private val onUpdateQty: (CartItem, Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<Any>()

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1
    }

    fun submitList(cartItems: List<CartItem>) {
        items.clear()
        cartItems.groupBy { it.type }.forEach { (type, groupItems) ->
            val header = when (type) {
                "spa" -> "SPA & WELLNESS"
                "reservation" -> "RESTAURANT RESERVATION"
                "transport" -> "TRANSPORTATION"
                "activity" -> "RESORT ACTIVITIES"
                "food" -> "ROOM SERVICE"
                "shop" -> "RESORT SHOP"
                "laundry" -> "LAUNDRY SERVICE"
                else -> type.uppercase().replace("_", " ")
            }
            items.add(header)
            items.addAll(groupItems)
        }
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (items[position] is String) TYPE_HEADER else TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_HEADER) {
            HeaderViewHolder(ItemCartHeaderBinding.inflate(inflater, parent, false))
        } else {
            ItemViewHolder(ItemCartLineBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HeaderViewHolder) {
            holder.bind(items[position] as String)
        } else if (holder is ItemViewHolder) {
            holder.bind(items[position] as CartItem)
        }
    }

    override fun getItemCount(): Int = items.size

    inner class HeaderViewHolder(private val binding: ItemCartHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(title: String) {
            binding.tvHeaderTitle.text = title
        }
    }

    inner class ItemViewHolder(private val binding: ItemCartLineBinding) :
        RecyclerView.ViewHolder(binding.root) {
        
        init {
            val internalFocusListener = View.OnFocusChangeListener { v, hasFocus ->
                FocusUtils.applyScaleAnimation(v, hasFocus)
            }
            binding.btnPlus.onFocusChangeListener = internalFocusListener
            binding.btnMinus.onFocusChangeListener = internalFocusListener
        }

        fun bind(item: CartItem) {
            val isBooking = item.type in listOf("spa", "reservation", "transport", "activity")
            
            binding.tvItemTitle.text = item.title
            binding.tvItemPrice.text = "₹${String.format("%.2f", item.price)}"
            
            if (isBooking) {
                binding.btnPlus.visibility = View.GONE
                binding.tvQty.text = item.slot ?: "Reserved"
                binding.tvQty.textSize = 14f
                binding.tvQty.setTextColor(ContextCompat.getColor(binding.root.context, R.color.gold_accent))
                binding.btnMinus.setImageResource(R.drawable.ic_close)
                binding.btnMinus.setOnClickListener { onUpdateQty(item, -1) } // will remove since qty is 1
            } else {
                binding.btnPlus.visibility = View.VISIBLE
                binding.btnPlus.setImageResource(R.drawable.ic_plus)
                binding.btnMinus.setImageResource(R.drawable.ic_minus)
                binding.tvQty.text = item.quantity.toString()
                binding.tvQty.textSize = 18f
                binding.tvQty.setTextColor(ContextCompat.getColor(binding.root.context, R.color.text_primary))
                
                binding.btnPlus.setOnClickListener { onUpdateQty(item, 1) }
                binding.btnMinus.setOnClickListener { onUpdateQty(item, -1) }
            }

            binding.tvLineTotal.text = "₹${String.format("%.2f", item.price * item.quantity)}"
        }
    }
}
