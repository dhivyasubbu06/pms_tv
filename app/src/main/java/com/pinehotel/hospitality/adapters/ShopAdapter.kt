package com.pinehotel.hospitality.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pinehotel.hospitality.databinding.ItemShopCardBinding
import com.pinehotel.hospitality.models.ResortProduct
import com.pinehotel.hospitality.utils.FocusUtils

class ShopAdapter(
    private val items: List<ResortProduct>,
    private val onAddClick: (ResortProduct) -> Unit
) : RecyclerView.Adapter<ShopAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemShopCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ResortProduct) {
            binding.tvName.text = item.name
            binding.tvPrice.text = "₹${String.format("%.2f", item.price)}"
            
            if (item.imageResId != 0) {
                binding.ivProduct.setImageResource(item.imageResId)
            }

            binding.root.isFocusable = true; binding.root.isFocusableInTouchMode = false; binding.root.setOnFocusChangeListener { view, hasFocus ->
                FocusUtils.applyScaleAnimation(view, hasFocus)
            }

            binding.btnAddToCart.setOnClickListener {
                onAddClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemShopCardBinding.inflate(
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
