package com.pinehotel.hospitality.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pinehotel.hospitality.databinding.ItemOrderConfirmationBinding
import com.pinehotel.hospitality.viewmodels.OrderResult

class OrderConfirmationAdapter(
    private val results: List<OrderResult>
) : RecyclerView.Adapter<OrderConfirmationAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemOrderConfirmationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(result: OrderResult) {
            binding.tvServiceType.text = result.serviceType.uppercase().replace("_", " ")
            binding.tvOrderId.text = result.orderId ?: "Order Received"
            binding.tvEstimatedTime.text = when(result.serviceType.lowercase()) {
                "food" -> "~30-45 mins"
                "laundry" -> "~2-4 hours"
                "spa" -> "At scheduled time"
                "transport" -> "Check for driver details"
                else -> "~30 mins"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemOrderConfirmationBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(results[position])
    }

    override fun getItemCount(): Int = results.size
}
