package com.pinehotel.hospitality.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pinehotel.hospitality.R
import com.pinehotel.hospitality.databinding.ItemFoodCardBinding
import com.pinehotel.hospitality.network.FoodMenuItem
import com.pinehotel.hospitality.utils.FocusUtils
import com.pinehotel.hospitality.utils.UrlUtils

import com.pinehotel.hospitality.viewmodels.CartItem
import com.pinehotel.hospitality.viewmodels.CartViewModel

class FoodAdapter(
    private val items: List<FoodMenuItem>,
    private val cartViewModel: CartViewModel,
    private val serviceType: String = "food"
) : RecyclerView.Adapter<FoodAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemFoodCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: FoodMenuItem) {
            binding.tvFoodName.text = item.title
            binding.tvFoodDesc.text = item.description ?: ""
            binding.tvPrice.text = "₹${String.format("%.2f", item.price)}"
            
            val fullImageUrl = UrlUtils.getFullImageUrl(item.imageUrl)
            if (!fullImageUrl.isNullOrEmpty()) {
                Glide.with(binding.ivFoodImage.context)
                    .load(fullImageUrl)
                    .placeholder(R.drawable.ic_room_service)
                    .into(binding.ivFoodImage)
            } else {
                binding.ivFoodImage.setImageResource(R.drawable.ic_room_service)
            }

            val internalFocusListener = View.OnFocusChangeListener { v, hasFocus ->
                val cardHasFocus = binding.btnAdd.hasFocus() || binding.btnPlus.hasFocus() || 
                               binding.btnMinus.hasFocus() || binding.tvQty.hasFocus()
                FocusUtils.applyScaleAnimation(binding.root, cardHasFocus)
            }
            
            binding.btnAdd.onFocusChangeListener = internalFocusListener
            binding.btnPlus.onFocusChangeListener = internalFocusListener
            binding.btnMinus.onFocusChangeListener = internalFocusListener
            binding.tvQty.onFocusChangeListener = internalFocusListener

            binding.root.isFocusable = false
            binding.root.isFocusableInTouchMode = false

            val cartKey = "${serviceType}_${item.id}"
            val cartItem = cartViewModel.cartState.value.itemsMap[cartKey]
            if (cartItem != null && cartItem.quantity > 0) {
                binding.vsStepper.displayedChild = 1
                binding.tvQty.text = cartItem.quantity.toString()
            } else {
                binding.vsStepper.displayedChild = 0
            }

            binding.btnAdd.setOnClickListener {
                val newItem = CartItem(item.id, item.title, item.price, 1, serviceType)
                cartViewModel.addItem(newItem)
                notifyItemChanged(bindingAdapterPosition)
            }

            binding.btnPlus.setOnClickListener {
                val currentItem = CartItem(item.id, item.title, item.price, 1, serviceType)
                cartViewModel.updateQuantity(currentItem, 1)
                notifyItemChanged(bindingAdapterPosition)
            }

            binding.btnMinus.setOnClickListener {
                val currentItem = CartItem(item.id, item.title, item.price, 1, serviceType)
                cartViewModel.updateQuantity(currentItem, -1)
                notifyItemChanged(bindingAdapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFoodCardBinding.inflate(
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
