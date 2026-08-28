package com.pinehotel.hospitality.ui

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pinehotel.hospitality.R
import com.pinehotel.hospitality.utils.FocusUtils

class LauncherPinkActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher_pink)

        setupHeader()
        setupGrid()
        setupBottomButtons()
        setupFooter()
    }

    private fun setupHeader() {
        val tvWelcome = findViewById<TextView>(R.id.tvWelcome)
        val welcomePrefix = "Welcome, "
        val name = "Dhivya" // Updated as per request
        val spannable = SpannableString("$welcomePrefix$name")
        val goldColor = resources.getColor(R.color.gold_accent, null)
        val whiteColor = Color.WHITE
        
        // "Welcome, " in white
        spannable.setSpan(
            ForegroundColorSpan(whiteColor),
            0,
            welcomePrefix.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        
        // Name in gold and italic
        spannable.setSpan(
            ForegroundColorSpan(goldColor),
            welcomePrefix.length,
            spannable.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
            StyleSpan(Typeface.ITALIC),
            welcomePrefix.length,
            spannable.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        tvWelcome.text = spannable
    }

    private fun setupGrid() {
        val rv = findViewById<RecyclerView>(R.id.rvLauncher)
        val items = listOf(
            LauncherItem("LIVE TV", "Watch Live Channels", R.drawable.ic_live_tv),
            LauncherItem("MOVIES", "Watch Movies & Shows", R.drawable.ic_movies),
            LauncherItem("SERVICE", "Order Food & Beverages", R.drawable.ic_room_service),
            LauncherItem("HOTEL INFORMATION", "About Our Hotel", R.drawable.ic_hotel_info),
            LauncherItem("WI-FI", "Connect to Internet", R.drawable.ic_wifi_small),
            LauncherItem("GALLERY", "Explore Hotel Photos", R.drawable.ic_gallery),
            LauncherItem("MESSAGES", "Messages from Hotel", R.drawable.ic_messages),
            LauncherItem("SETTINGS", "System Settings", R.drawable.ic_settings)
        )

        rv.layoutManager = GridLayoutManager(this, 4)
        rv.adapter = LauncherPinkAdapter(items)
        
        rv.post {
            rv.findViewHolderForAdapterPosition(0)?.itemView?.requestFocus()
        }
    }

    private fun setupBottomButtons() {
        val btnTv = findViewById<View>(R.id.btnActionTv)
        val btnBookings = findViewById<View>(R.id.btnActionBookings)

        listOf(btnTv, btnBookings).forEach { btn ->
            btn.isFocusable = true
            btn.setOnFocusChangeListener { v, hasFocus ->
                FocusUtils.applyScaleAnimation(v, hasFocus)
            }
        }
    }

    private fun setupFooter() {
        val tvFooter = findViewById<TextView>(R.id.tvFooterGuest)
        val text = "Guest: Ramya Velu   |   Room: 205"
        val spannable = SpannableString(text)
        val goldColor = resources.getColor(R.color.gold_accent, null)
        
        val start = text.indexOf("Ramya Velu")
        if (start != -1) {
            spannable.setSpan(
                ForegroundColorSpan(goldColor),
                start,
                start + "Ramya Velu".length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        tvFooter.text = spannable
    }

    data class LauncherItem(val title: String, val subtitle: String, val icon: Int)

    inner class LauncherPinkAdapter(private val items: List<LauncherItem>) :
        RecyclerView.Adapter<LauncherPinkAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val icon: ImageView = view.findViewById(R.id.cardIcon)
            val title: TextView = view.findViewById(R.id.cardTitle)
            val subtitle: TextView = view.findViewById(R.id.cardSubtitle)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_launcher_card_pink, parent, false)
            return ViewHolder(v)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.title.text = item.title
            holder.subtitle.text = item.subtitle
            holder.icon.setImageResource(item.icon)

            holder.itemView.setOnFocusChangeListener { v, hasFocus ->
                FocusUtils.applyScaleAnimation(v, hasFocus)
            }
        }

        override fun getItemCount() = items.size
    }
}