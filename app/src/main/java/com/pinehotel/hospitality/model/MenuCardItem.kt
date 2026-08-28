package com.pinehotel.hospitality.model

import androidx.annotation.DrawableRes

/**
 * Identifies which destination a card routes to. Kept as an enum (rather than a
 * raw Activity class reference) so the data model has zero Android UI dependencies
 * and the adapter/activity stay in charge of the actual navigation.
 */
enum class MenuDestination {
    LIVE_TV,
    MOVIES,
    ROOM_SERVICE,
    HOUSEKEEPING,
    HOTEL_INFO,
    WIFI,
    GALLERY,
    MESSAGES,
    SETTINGS,
    BOOKINGS
}

/**
 * A single focusable tile in the home screen grid.
 */
data class MenuCardItem(
    val id: MenuDestination,
    val title: String,
    val subtitle: String,
    @DrawableRes val iconRes: Int
)
