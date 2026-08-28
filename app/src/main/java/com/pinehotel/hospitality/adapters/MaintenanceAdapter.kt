package com.pinehotel.hospitality.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pinehotel.hospitality.databinding.ItemMaintenanceCardBinding
import com.pinehotel.hospitality.models.MaintenanceIssue
import com.pinehotel.hospitality.utils.FocusUtils

class MaintenanceAdapter(
    private val items: List<MaintenanceIssue>,
    private val onReportClick: (MaintenanceIssue) -> Unit
) : RecyclerView.Adapter<MaintenanceAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemMaintenanceCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MaintenanceIssue) {
            binding.tvName.text = item.name
            
            if (item.iconResId != 0) {
                binding.ivIcon.setImageResource(item.iconResId)
            }

            binding.root.isFocusable = true; binding.root.isFocusableInTouchMode = false; binding.root.setOnFocusChangeListener { view, hasFocus ->
                FocusUtils.applyScaleAnimation(view, hasFocus)
            }

            binding.btnReport.setOnClickListener {
                onReportClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMaintenanceCardBinding.inflate(
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
