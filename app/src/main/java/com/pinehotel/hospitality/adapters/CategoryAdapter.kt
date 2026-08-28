package com.pinehotel.hospitality.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pinehotel.hospitality.R
import com.pinehotel.hospitality.utils.FocusUtils

class CategoryAdapter(
    private val categories: List<String>,
    private val onCategoryClick: (String) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    private var selectedPosition = 0

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val root: LinearLayout = itemView.findViewById(R.id.categoryRoot)
        val tvIcon: TextView = itemView.findViewById(R.id.tvIcon)
        val tvName: TextView = itemView.findViewById(R.id.tvCategoryName)

        fun bind(category: String, position: Int) {
            tvName.text = category
            
            // Map icons based on your backend ENTERTAINMENT_CAT_ICONS
            val icon = when (category.lowercase()) {
                "indoor" -> "🎮"
                "outdoor" -> "⛷️"
                "water" -> "🏊"
                "kids" -> "🎠"
                "night" -> "🌙"
                else -> null
            }

            if (icon != null) {
                tvIcon.text = icon
                tvIcon.visibility = View.VISIBLE
            } else {
                tvIcon.visibility = View.GONE
            }

            itemView.isFocusable = true
            itemView.isFocusableInTouchMode = false
            itemView.setOnFocusChangeListener { view, hasFocus ->
                FocusUtils.applyScaleAnimation(view, hasFocus)
            }

            itemView.setOnClickListener {
                selectedPosition = position
                notifyDataSetChanged()
                onCategoryClick(category)
            }
            
            // Highlight selected category
            val isSelected = position == selectedPosition
            root.alpha = if (isSelected) 1.0f else 0.6f
            tvName.setTextColor(
                if (isSelected) 
                    itemView.context.getColor(R.color.wine_dark)
                else 
                    itemView.context.getColor(R.color.text_primary)
            )
            
            if (isSelected) {
                root.setBackgroundResource(R.drawable.bg_gold_pill)
            } else {
                root.setBackgroundResource(R.drawable.bg_room_pill)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(categories[position], position)
    }

    override fun getItemCount(): Int = categories.size
}