package com.pinehotel.hospitality.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pinehotel.hospitality.R
import com.pinehotel.hospitality.databinding.ItemRequestCardBinding
import com.pinehotel.hospitality.network.GuestOrder
import com.pinehotel.hospitality.utils.FocusUtils

class RequestAdapter(
    private val items: List<GuestOrder>
) : RecyclerView.Adapter<RequestAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemRequestCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: GuestOrder) {
            binding.tvRequestId.text = "#${item.id ?: 0}"
            binding.tvServiceName.text = item.serviceType ?: "Request"
            binding.tvDateTime.text = if (!item.orderedAt.isNullOrEmpty()) item.orderedAt else (item.bookedAt ?: "")
            
            val status = item.status?.lowercase() ?: "pending"
            binding.tvStatus.text = status.uppercase()
            
            val statusColor = when (status) {
                "done", "completed" -> R.color.status_done
                "accepted", "in progress" -> R.color.status_in_progress
                "pending" -> R.color.status_pending
                else -> R.color.rose_pink_dark
            }
            binding.tvStatus.setBackgroundResource(statusColor)

            binding.root.isFocusable = true; binding.root.isFocusableInTouchMode = false; binding.root.setOnFocusChangeListener { view, hasFocus ->
                FocusUtils.applyScaleAnimation(view, hasFocus)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRequestCardBinding.inflate(
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
