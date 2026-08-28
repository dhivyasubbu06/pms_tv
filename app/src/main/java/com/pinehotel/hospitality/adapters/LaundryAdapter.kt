package com.pinehotel.hospitality.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pinehotel.hospitality.databinding.ItemLaundryCardBinding
import com.pinehotel.hospitality.models.LaundryService
import com.pinehotel.hospitality.utils.FocusUtils

class LaundryAdapter(
    private val items: List<LaundryService>,
    private val onRequestClick: (LaundryService) -> Unit
) : RecyclerView.Adapter<LaundryAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemLaundryCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: LaundryService) {
            binding.tvName.text = item.name
            binding.tvDeliveryTime.text = "Delivery: ${item.deliveryTime}"
            binding.tvPrice.text = "₹${String.format("%.2f", item.price)}"
            
            binding.root.isFocusable = true; binding.root.isFocusableInTouchMode = false; binding.root.setOnFocusChangeListener { view, hasFocus ->
                FocusUtils.applyScaleAnimation(view, hasFocus)
            }

            binding.btnRequest.setOnClickListener {
                onRequestClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLaundryCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
