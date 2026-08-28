package com.pinehotel.hospitality.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.pinehotel.hospitality.R
import com.pinehotel.hospitality.model.MenuCardItem
import com.pinehotel.hospitality.utils.FocusUtils

/**
 * Adapter for the main navigation grid.
 *
 * Focus handling lives here rather than relying purely on XML state-list
 * drawables, because we also want a scale animation — an OnFocusChangeListener
 * gives us a single place to (a) run the animation, (b) swap the gold-glow
 * background, and (c) elevate the card so its shadow reads correctly.
 */
class MenuCardAdapter(
    private val items: List<MenuCardItem>,
    private val onCardClicked: (MenuCardItem) -> Unit,
    private val onCardLongClicked: ((MenuCardItem) -> Unit)? = null
) : RecyclerView.Adapter<MenuCardAdapter.CardViewHolder>() {

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardRoot: CardView = itemView.findViewById(R.id.cardRoot)
        val icon: ImageView = itemView.findViewById(R.id.cardIcon)
        val title: TextView = itemView.findViewById(R.id.cardTitle)
        val subtitle: TextView = itemView.findViewById(R.id.cardSubtitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_menu_card, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = items[position]

        holder.icon.setImageResource(item.iconRes)
        holder.title.text = item.title
        holder.subtitle.text = item.subtitle

        holder.itemView.isFocusable = true
        holder.itemView.isFocusableInTouchMode = false

        holder.itemView.setOnClickListener { onCardClicked(item) }

        holder.itemView.setOnLongClickListener {
            onCardLongClicked?.invoke(item)
            true
        }

        holder.itemView.setOnFocusChangeListener { view, hasFocus ->
            FocusUtils.applyScaleAnimation(view, hasFocus)
        }
    }

    override fun getItemCount(): Int = items.size
}
