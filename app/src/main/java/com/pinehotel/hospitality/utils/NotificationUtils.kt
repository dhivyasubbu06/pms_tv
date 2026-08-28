package com.pinehotel.hospitality.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import com.pinehotel.hospitality.R

object NotificationUtils {

    fun showSuccessNotification(view: View, message: String) {
        val snackbar = Snackbar.make(view, "", Snackbar.LENGTH_LONG)
        
        val snackbarView = snackbar.view
        val textView = snackbarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        textView.visibility = View.INVISIBLE

        val inflater = LayoutInflater.from(view.context)
        val customView = inflater.inflate(R.layout.layout_notification_bar, null)
        customView.findViewById<TextView>(R.id.tvNotificationMessage).text = message
        
        snackbarView.setBackgroundColor(android.graphics.Color.TRANSPARENT)
        
        val layoutParams = customView.layoutParams as? ViewGroup.MarginLayoutParams
            ?: ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        
        // Add to the snackbar layout
        (snackbarView as ViewGroup).addView(customView, 0)
        
        snackbar.show()
    }
}
