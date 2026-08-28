package com.pinehotel.hospitality.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pinehotel.hospitality.R
import com.pinehotel.hospitality.databinding.ItemTransportCardBinding
import com.pinehotel.hospitality.network.BarItem
import com.pinehotel.hospitality.utils.FocusUtils
import com.pinehotel.hospitality.utils.PriceUtils
import com.pinehotel.hospitality.utils.UrlUtils
import com.pinehotel.hospitality.viewmodels.CartItem
import com.pinehotel.hospitality.viewmodels.CartViewModel

class TransportAdapter(
    private val items: List<BarItem>,
    private val cartViewModel: CartViewModel,
    private val itemType: String = "transport",
    private val buttonText: String = "ADD"
) : RecyclerView.Adapter<TransportAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemTransportCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: BarItem) {
            binding.tvName.text = item.title
            val price = PriceUtils.getDynamicPrice(item.price ?: 0.0, itemType, item.title)
            binding.tvDescription.text = "Price: ₹${String.format("%,.0f", price)}"
            
            val fallbackIcon = when {
                item.imageUrl == "ic_transport_pickup" || item.title?.contains("Pickup", true) == true -> R.drawable.ic_transport_pickup
                item.imageUrl == "ic_transport_drop" || item.title?.contains("Drop", true) == true -> R.drawable.ic_transport_drop
                item.imageUrl == "ic_transport_taxi" || item.title?.contains("Taxi", true) == true -> R.drawable.ic_transport_taxi
                item.imageUrl == "ic_transport_shuttle" || item.title?.contains("Shuttle", true) == true -> R.drawable.ic_transport_shuttle
                itemType == "laundry" -> R.drawable.ic_laundry
                else -> R.drawable.ic_transport
            }

            val fullImageUrl = UrlUtils.getFullImageUrl(item.imageUrl)
            if (!fullImageUrl.isNullOrEmpty() && fullImageUrl.startsWith("http")) {
                binding.ivIcon.imageTintList = null 
                Glide.with(binding.ivIcon.context)
                    .load(fullImageUrl)
                    .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.ALL)
                    .signature(com.bumptech.glide.signature.ObjectKey(item.imageUrl ?: ""))
                    .placeholder(fallbackIcon)
                    .error(fallbackIcon)
                    .into(binding.ivIcon)
            } else {
                binding.ivIcon.setImageResource(fallbackIcon)
                binding.ivIcon.imageTintList = null
            }

            binding.root.isFocusable = true
            binding.root.isFocusableInTouchMode = false
            binding.root.setOnFocusChangeListener { view, hasFocus ->
                FocusUtils.applyScaleAnimation(view, hasFocus)
            }

            binding.root.setOnClickListener {
                val newItem = CartItem(item.id ?: 0, item.title ?: "", price, 1, itemType)
                cartViewModel.addItem(newItem)
                android.widget.Toast.makeText(it.context, "${item.title} added to cart", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTransportCardBinding.inflate(
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
