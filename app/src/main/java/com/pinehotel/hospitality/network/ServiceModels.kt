package com.pinehotel.hospitality.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GuestInfo(
    @Json(name = "room_no") val roomNo: Int,
    @Json(name = "guest_name") val guestName: String,
    @Json(name = "check_in") val checkIn: String,
    @Json(name = "check_out") val checkOut: String,
    @Json(name = "days_left") val daysLeft: Int
)

@JsonClass(generateAdapter = true)
data class ServiceItem(
    val id: Int,
    val title: String,
    val description: String?,
    @Json(name = "image_url") val imageUrl: String?
)

@JsonClass(generateAdapter = true)
data class ServiceTile(
    val id: Int,
    val title: String,
    @Json(name = "image_url") val imageUrl: String?
)

@JsonClass(generateAdapter = true)
data class FoodMenuItem(
    val id: Int,
    val title: String,
    val description: String?,
    val price: Double,
    @Json(name = "image_url") val imageUrl: String?,
    val category: String?
)

@JsonClass(generateAdapter = true)
data class SpaServiceItem(
    val id: Int? = 0,
    val title: String? = "",
    val description: String? = "",
    val price: Double? = 0.0,
    val duration: String? = "",
    val category: String? = "",
    val slot1: String? = "",
    val slot2: String? = "",
    val slot3: String? = "",
    @Json(name = "image_url") val imageUrl: String? = ""
)

@JsonClass(generateAdapter = true)
data class ActivityItem(
    val id: Int? = 0,
    val title: String? = "",
    val description: String? = "",
    @Json(name = "time_slot") val timeSlot: String? = "",
    @Json(name = "image_url") val imageUrl: String? = ""
)

@JsonClass(generateAdapter = true)
data class DineItem(
    val id: Int? = 0,
    val title: String? = "",
    val description: String? = "",
    val price: Double? = 0.0,
    val slot1: String? = null,
    val slot2: String? = null,
    val slot3: String? = null,
    @Json(name = "image_url") val imageUrl: String? = ""
)

@JsonClass(generateAdapter = true)
data class BarItem(
    val id: Int? = 0,
    val title: String? = "",
    val price: Double? = 0.0,
    @Json(name = "image_url") val imageUrl: String? = "",
    val description: String? = "",
    val slot1: String? = null,
    val slot2: String? = null,
    val slot3: String? = null
)

// --- Specific Request Models ---

@JsonClass(generateAdapter = true)
data class OrderRequestItem(
    val id: Int,
    val name: String,
    val qty: Int,
    val price: Int,
    val slot: String? = null
)

@JsonClass(generateAdapter = true)
data class OrderRequest(
    @Json(name = "room_no") val roomNo: Int,
    val items: List<OrderRequestItem>,
    val total: Int,
    @Json(name = "order_type") val serviceType: String
)

@JsonClass(generateAdapter = true)
data class HousekeepingRequest(
    @Json(name = "room_no") val roomNo: Int,
    @Json(name = "service_id") val serviceId: Int,
    @Json(name = "service_title") val serviceTitle: String,
    val note: String
)

@JsonClass(generateAdapter = true)
data class SpaBookingRequest(
    @Json(name = "room_no") val roomNo: Int,
    @Json(name = "item_id") val itemId: Int,
    @Json(name = "item_title") val itemTitle: String,
    val category: String,
    val slot: String,
    val price: Int = 0
)

@JsonClass(generateAdapter = true)
data class DineBookingRequest(
    @Json(name = "room_no") val roomNo: Int,
    @Json(name = "item_id") val itemId: Int,
    @Json(name = "item_name") val itemName: String,
    val occasion: String,
    val slot: String
)

@JsonClass(generateAdapter = true)
data class RestaurantReservationRequest(
    @Json(name = "room_no") val roomNo: Int,
    @Json(name = "restaurant_name") val restaurantName: String,
    val slot: String,
    @Json(name = "guests_count") val guestsCount: Int,
    val note: String
)

@JsonClass(generateAdapter = true)
data class GenericBookingRequest(
    @Json(name = "item_id") val itemId: Int,
    @Json(name = "selected_slot") val selectedSlot: String,
    val category: String // "activity", "entertainment", "spa"
)

@JsonClass(generateAdapter = true)
data class EntertainmentBookingRequest(
    @Json(name = "room_no") val roomNo: Int,
    @Json(name = "item_id") val itemId: Int,
    @Json(name = "item_title") val itemTitle: String,
    val slot: String,
    val guests: Int = 1
)

@JsonClass(generateAdapter = true)
data class ActivityBookingRequest(
    @Json(name = "room_no") val roomNo: Int,
    @Json(name = "activity_id") val activityId: Int,
    val title: String,
    @Json(name = "time_slot") val timeSlot: String
)

@JsonClass(generateAdapter = true)
data class TransportRequest(
    @Json(name = "room_no") val roomNo: Int,
    @Json(name = "service_id") val serviceId: Int,
    @Json(name = "service_title") val serviceTitle: String,
    @Json(name = "pickup_time") val pickupTime: String,
    val note: String
)

@JsonClass(generateAdapter = true)
data class LaundryRequest(
    @Json(name = "room_no") val roomNo: Int,
    @Json(name = "service_id") val serviceId: Int,
    @Json(name = "service_title") val serviceTitle: String,
    val price: Int,
    val note: String
)

@JsonClass(generateAdapter = true)
data class ConciergeRequest(
    @Json(name = "room_no") val roomNo: Int,
    @Json(name = "request_type") val requestType: String,
    val description: String
)

@JsonClass(generateAdapter = true)
data class MaintenanceRequest(
    @Json(name = "room_no") val roomNo: Int,
    @Json(name = "issue_name") val issueName: String,
    val description: String
)

@JsonClass(generateAdapter = true)
data class ShopOrderRequest(
    @Json(name = "room_no") val roomNo: Int,
    @Json(name = "product_id") val productId: Int,
    @Json(name = "product_title") val productTitle: String,
    val price: Int,
    val qty: Int
)

// --- Response Models ---

@JsonClass(generateAdapter = true)
data class ServiceResponse(
    val status: String? = null,
    val success: Boolean? = null,
    val message: String? = null,
    val detail: Any? = null
) {
    val isSuccess: Boolean get() = status == "success" || success == true
    
    val displayMessage: String
        get() = message ?: detail?.toString() ?: "Request processed"
}

@JsonClass(generateAdapter = true)
data class Activity(
    val id: Int,
    val title: String,
    @Json(name = "time_slot") val timeSlot: String?,
    @Json(name = "is_announcement") val isAnnouncement: Boolean
)

@JsonClass(generateAdapter = true)
data class GuestOrder(
    val id: Int? = 0,
    @Json(name = "room_no") val roomNo: Int? = 0,
    @Json(name = "type") val type: String? = "",
    @Json(name = "service_type") val serviceType: String? = "",
    @Json(name = "order_type") val orderType: String? = "",
    val details: String? = "",
    val title: String? = "",
    val items: String? = null,
    val status: String? = "pending",
    @Json(name = "ordered_at") val orderedAt: String? = "",
    @Json(name = "booked_at") val bookedAt: String? = "",
    val slot: String? = "",
    val amount: Int? = 0,
    val price: Int? = 0,
    val total: Int? = 0
)

@JsonClass(generateAdapter = true)
data class UnifiedRequest(
    @Json(name = "req_id") val reqId: String,
    @Json(name = "service_name") val serviceName: String,
    val date: String,
    val time: String,
    val status: String,
    val details: String
)

@JsonClass(generateAdapter = true)
data class OrderTotals(
    val food: Int,
    val bar: Int,
    val spa: Int,
    val entertainment: Int,
    val dine: Int,
    val grand: Int
)

@JsonClass(generateAdapter = true)
data class MyOrdersResponse(
    @Json(name = "room_no") val roomNo: Int,
    val totals: OrderTotals?,
    val orders: List<GuestOrder>,
    @Json(name = "food_orders") val foodOrders: List<GuestOrder>?,
    @Json(name = "bar_orders") val barOrders: List<GuestOrder>?,
    @Json(name = "spa_bookings") val spaBookings: List<GuestOrder>?,
    @Json(name = "entertainment_bookings") val entertainmentBookings: List<GuestOrder>?,
    @Json(name = "activity_bookings") val activityBookings: List<GuestOrder>?,
    @Json(name = "dine_bookings") val dineBookings: List<GuestOrder>?,
    @Json(name = "meal_plan") val mealPlan: String?
)
