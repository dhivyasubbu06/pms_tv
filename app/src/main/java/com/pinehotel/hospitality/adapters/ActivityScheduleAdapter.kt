package com.pinehotel.hospitality.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pinehotel.hospitality.R
import com.pinehotel.hospitality.network.Activity
import com.pinehotel.hospitality.utils.FocusUtils

class ActivityScheduleAdapter(
    private var items: List<Activity>,
    private val onItemClick: (Activity) -> Unit
) : RecyclerView.Adapter<ActivityScheduleAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvAnnouncement: TextView = itemView.findViewById(R.id.tvAnnouncement)

        fun bind(item: Activity) {
            tvTitle.text = item.title
            tvTime.text = item.timeSlot ?: "--:--"
            tvAnnouncement.visibility = if (item.isAnnouncement) View.VISIBLE else View.GONE

            itemView.setOnClickListener {
                if (!item.isAnnouncement) {
                    onItemClick(item)
                }
            }

            itemView.setOnFocusChangeListener { view, hasFocus ->
                FocusUtils.applyScaleAnimation(view, hasFocus)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_activity_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<Activity>) {
        this.items = newItems
        notifyDataSetChanged()
    }
}
