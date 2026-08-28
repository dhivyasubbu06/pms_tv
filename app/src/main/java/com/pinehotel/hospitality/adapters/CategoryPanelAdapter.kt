package com.pinehotel.hospitality.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pinehotel.hospitality.R
import com.pinehotel.hospitality.databinding.ItemCategoryPanelBinding
import com.pinehotel.hospitality.network.GuestOrder

data class CategoryData(
    val title: String,
    val emoji: String,
    val bookings: List<GuestOrder>
)

class CategoryPanelAdapter(private val categories: List<CategoryData>) :
    RecyclerView.Adapter<CategoryPanelAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemCategoryPanelBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: CategoryData) {
            binding.tvCategoryTitle.text = data.title
            binding.tvCategoryEmoji.text = data.emoji
            
            if (data.bookings.isEmpty()) {
                binding.tvEmpty.visibility = View.VISIBLE
                binding.rvBookings.visibility = View.GONE
            } else {
                binding.tvEmpty.visibility = View.GONE
                binding.rvBookings.visibility = View.VISIBLE
                binding.rvBookings.layoutManager = LinearLayoutManager(binding.root.context)
                binding.rvBookings.adapter = BookingRowAdapter(data.bookings)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCategoryPanelBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    override fun getItemCount(): Int = categories.size
}
