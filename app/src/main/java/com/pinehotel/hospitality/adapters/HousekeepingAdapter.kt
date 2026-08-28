package com.pinehotel.hospitality.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pinehotel.hospitality.R
import com.pinehotel.hospitality.databinding.ItemHousekeepingCardBinding
import com.pinehotel.hospitality.network.ServiceItem
import com.pinehotel.hospitality.utils.FocusUtils
import com.pinehotel.hospitality.utils.UrlUtils

class HousekeepingAdapter(
    private val items: List<ServiceItem>,
    private val onRequestClick: (ServiceItem) -> Unit
) : RecyclerView.Adapter<HousekeepingAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemHousekeepingCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ServiceItem) {
            binding.tvTitle.text = item.title
            binding.tvDescription.text = item.description ?: ""
            
            // PRIORITY: 1. Portal Image -> 2. High-quality 3D Icon -> 3. Default Placeholder
            val fullImageUrl = UrlUtils.getFullImageUrl(item.imageUrl)
            
            val fallbackIcon = when {
                item.title.contains("Cleaning", true) -> R.drawable.ic_housekeeping_broom
                item.title.contains("Towels", true) -> R.drawable.ic_housekeeping_spray
                item.title.contains("Pillow", true) -> R.drawable.ic_housekeeping_pillow
                item.title.contains("Blanket", true) -> R.drawable.ic_housekeeping_blanket
                item.title.contains("Linen", true) || item.title.contains("Bed", true) -> R.drawable.ic_housekeeping_bed
                else -> R.drawable.ic_housekeeping
            }

            if (!fullImageUrl.isNullOrEmpty() && fullImageUrl.startsWith("http")) {
                binding.ivIcon.imageTintList = null 
                Glide.with(binding.ivIcon.context)
                    .load(fullImageUrl)
                    .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.ALL)
                    .signature(com.bumptech.glide.signature.ObjectKey(item.imageUrl!!))
                    .placeholder(fallbackIcon)
                    .error(fallbackIcon)
                    .into(binding.ivIcon)
            } else {
                binding.ivIcon.setImageResource(fallbackIcon)
                binding.ivIcon.imageTintList = null
            }

            binding.root.isFocusable = true; binding.root.isFocusableInTouchMode = false; binding.root.setOnFocusChangeListener { view, hasFocus ->
                FocusUtils.applyScaleAnimation(view, hasFocus)
            }

            // Handle the click on the ENTIRE CARD for Android TV remote compatibility
            binding.root.setOnClickListener {
                onRequestClick(item)
            }

            // Also keep the button click just in case, though root covers it
            binding.btnRequest.setOnClickListener {
                onRequestClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHousekeepingCardBinding.inflate(
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
