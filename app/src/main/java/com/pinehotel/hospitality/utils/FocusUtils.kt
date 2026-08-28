package com.pinehotel.hospitality.utils

import android.view.View

object FocusUtils {
    fun applyScaleAnimation(view: View, hasFocus: Boolean) {
        // Only zoom in and out to indicate focus, no color highlights
        val scale = if (hasFocus) 1.08f else 1.00f
        view.animate()
            .scaleX(scale)
            .scaleY(scale)
            .setDuration(200)
            .start()
    }
}
