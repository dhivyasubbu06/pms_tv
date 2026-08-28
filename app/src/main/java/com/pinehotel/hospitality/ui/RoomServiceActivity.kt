package com.pinehotel.hospitality.ui

import android.os.Bundle
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.pinehotel.hospitality.R

/**
 * Stub destination screen for "Room Service".
 * Replace this body with the real screen UI; the click handler and manifest
 * entry are already wired up from the home screen.
 */
class RoomServiceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = FrameLayout(this).apply {
            setBackgroundColor(getColor(R.color.wine_dark))
        }
        val label = TextView(this).apply {
            text = "Room Service"
            textSize = 28f
            setTextColor(getColor(R.color.gold_bright))
            gravity = Gravity.CENTER
            isFocusable = true
            isFocusableInTouchMode = true
        }
        root.addView(
            label,
            FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        )
        setContentView(root)
        label.requestFocus()
    }
}
