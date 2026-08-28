package com.pinehotel.hospitality.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Anything that can be added to the cart implements this — lets CartViewModel
 * and the adapter work generically across Food, Spa, Laundry, Shop, Transport
 * without needing one unified data class.
 */
interface CartOrderable {
    val cartId: String   // unique across ALL categories, e.g. "food_12", "spa_3"
    val cartTitle: String
    val cartPrice: Double
    val cartImageRes: Int
}

@Parcelize
data class ServiceItem(
    val id: Int,
    val name: String,
    val description: String,
    val iconResId: Int,
    val destinationId: Int
) : Parcelable

@Parcelize
data class FoodItem(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val imageResId: Int,
    val category: String
) : Parcelable, CartOrderable {
    override val cartId get() = "food_$id"
    override val cartTitle get() = name
    override val cartPrice get() = price
    override val cartImageRes get() = imageResId
}

@Parcelize
data class HousekeepingService(
    val id: Int,
    val name: String,
    val description: String,
    val iconResId: Int
) : Parcelable

@Parcelize
data class SpaService(
    val id: Int,
    val name: String,
    val duration: String,
    val price: Double,
    val imageResId: Int
) : Parcelable, CartOrderable {
    override val cartId get() = "spa_$id"
    override val cartTitle get() = name
    override val cartPrice get() = price
    override val cartImageRes get() = imageResId
}

@Parcelize
data class Restaurant(
    val id: Int,
    val name: String,
    val description: String,
    val imageResId: Int
) : Parcelable

@Parcelize
data class ResortActivity(
    val id: Int,
    val name: String,
    val description: String,
    val timing: String,
    val imageResId: Int
) : Parcelable

@Parcelize
data class TransportService(
    val id: Int,
    val name: String,
    val description: String,
    val iconResId: Int,
    val price: Double = 0.0
) : Parcelable, CartOrderable {
    override val cartId get() = "transport_$id"
    override val cartTitle get() = name
    override val cartPrice get() = price
    override val cartImageRes get() = iconResId
}

@Parcelize
data class LaundryService(
    val id: Int,
    val name: String,
    val price: Double,
    val deliveryTime: String
) : Parcelable, CartOrderable {
    override val cartId get() = "laundry_$id"
    override val cartTitle get() = name
    override val cartPrice get() = price
    override val cartImageRes get() = 0  // no image field on this class — icon-less card
}

@Parcelize
data class ResortProduct(
    val id: Int,
    val name: String,
    val price: Double,
    val imageResId: Int
) : Parcelable, CartOrderable {
    override val cartId get() = "shop_$id"
    override val cartTitle get() = name
    override val cartPrice get() = price
    override val cartImageRes get() = imageResId
}

@Parcelize
data class MaintenanceIssue(
    val id: Int,
    val name: String,
    val iconResId: Int,
    val description: String? = null,
    val imageUrl: String? = null
) : Parcelable

@Parcelize
data class UserRequest(
    val requestId: String,
    val serviceName: String,
    val date: String,
    val time: String,
    val status: String
) : Parcelable

@Parcelize
data class LiveTvChannel(
    val id: Int,
    val name: String,
    val category: String,
    val logoResId: Int,
    val streamUrl: String
) : Parcelable
