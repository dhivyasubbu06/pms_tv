package com.pinehotel.hospitality.adapters

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import android.view.View
import com.pinehotel.hospitality.R
import com.pinehotel.hospitality.databinding.ItemServiceCardBinding
import com.pinehotel.hospitality.network.ServiceTile
import com.pinehotel.hospitality.network.UnifiedRequest
import com.pinehotel.hospitality.utils.FocusUtils
import com.pinehotel.hospitality.utils.UrlUtils

class ServiceAdapter(
    private var items: List<ServiceTile>,
    private val onItemClick: (ServiceTile) -> Unit
) : RecyclerView.Adapter<ServiceAdapter.ViewHolder>() {

    fun updateData(newItems: List<ServiceTile>) {
        this.items = newItems
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemServiceCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ServiceTile) {
            binding.tvName.text = item.title
            binding.tvDescription.text = "" 

            val fullImageUrl = UrlUtils.getFullImageUrl(item.imageUrl)
            
            // Always set scaleType to CENTER_CROP for photos
            binding.ivIcon.scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
            
            if (!fullImageUrl.isNullOrEmpty()) {
                binding.ivIcon.imageTintList = null 
                Glide.with(binding.ivIcon.context)
                    .load(fullImageUrl)
                    .placeholder(R.drawable.ic_room_service)
                    .error(R.drawable.ic_room_service)
                    .into(binding.ivIcon)
            } else {
                // If it's a vector placeholder, use CENTER_INSIDE and gold tint
                binding.ivIcon.scaleType = android.widget.ImageView.ScaleType.CENTER_INSIDE
                binding.ivIcon.setImageResource(R.drawable.ic_room_service)
                binding.ivIcon.imageTintList = ColorStateList.valueOf(
                    binding.ivIcon.context.getColor(R.color.gold_accent)
                )
            }

            binding.root.isFocusable = true
            binding.root.isFocusableInTouchMode = false
            binding.root.setOnFocusChangeListener { view, hasFocus ->
                FocusUtils.applyScaleAnimation(view, hasFocus)
            }

            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemServiceCardBinding.inflate(
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
