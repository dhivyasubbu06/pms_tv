package com.pinehotel.hospitality.network

import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET("api/guests/current")
    suspend fun getGuestInfo(): List<GuestInfo>

    @GET("api/services")
    suspend fun getServices(): List<ServiceTile>

    // 1. Order API (Food, Bar, etc.)
    @GET("api/food-items")
    suspend fun getFoodItems(@Query("category") category: String? = null): List<FoodMenuItem>
    @GET("api/bar-items")
    suspend fun getBarItems(@Query("category") category: String? = null): List<BarItem>
    @POST("api/order")
    suspend fun submitOrder(@Body request: OrderRequest): ServiceResponse

    // 2. Housekeeping
    @GET("api/room-service-items")
    suspend fun getRoomServiceItems(): List<ServiceItem>
    @POST("api/housekeeping-request")
    suspend fun submitHousekeepingRequest(@Body request: HousekeepingRequest): ServiceResponse

    // 3. Spa & Wellness
    @GET("api/spa-items")
    suspend fun getSpaItems(@Query("category") category: String? = null): List<SpaServiceItem>
    @POST("api/spa-booking")
    suspend fun submitSpaBooking(@Body request: SpaBookingRequest): ServiceResponse

    // 4. Restaurant Reservation
    @GET("api/dine-items")
    suspend fun getDineItems(@Query("occasion") occasion: String? = null): List<DineItem>
    @POST("api/dine-booking")
    suspend fun submitDineBooking(@Body request: DineBookingRequest): ServiceResponse

    // 5. Activity & Entertainment Booking
    @GET("api/entertainment-items")
    suspend fun getEntertainmentItems(@Query("category") category: String? = "all"): List<SpaServiceItem>

    @GET("api/activities")
    suspend fun getActivities(@Query("category") category: String? = null): List<Activity>

    @POST("api/entertainment-booking")
    suspend fun submitEntertainmentBooking(@Body request: EntertainmentBookingRequest): ServiceResponse

    @POST("api/activity-booking")
    suspend fun submitActivityBooking(@Body request: ActivityBookingRequest): ServiceResponse

    @POST("api/guest/{room_id}/bookings")
    suspend fun submitBooking(
        @Path("room_id") roomId: Int,
        @Body request: GenericBookingRequest
    ): ServiceResponse

    @GET("api/transport-items")
    suspend fun getTransportItems(): List<BarItem>
    @POST("api/transport-request")
    suspend fun submitTransportRequest(@Body request: TransportRequest): ServiceResponse

    // 7. Laundry Service
    @GET("api/laundry-items")
    suspend fun getLaundryItems(): List<BarItem>
    @POST("api/laundry-request")
    suspend fun submitLaundryRequest(@Body request: LaundryRequest): ServiceResponse

    // 8. Concierge Service
    @POST("api/concierge-request")
    suspend fun submitConciergeRequest(@Body request: ConciergeRequest): ServiceResponse

    // 9. Maintenance Requests
    @POST("api/maintenance-request")
    suspend fun submitMaintenanceRequest(@Body request: MaintenanceRequest): ServiceResponse

    // 10. Resort Shop
    @GET("api/shop-items")
    suspend fun getShopItems(): List<BarItem>
    @POST("api/shop-order")
    suspend fun submitShopOrder(@Body request: ShopOrderRequest): ServiceResponse

    // 11. My Orders Tracker (Structured)
    @GET("api/my-orders/{room_no}")
    suspend fun getMyOrders(@Path("room_no") roomNo: Int): MyOrdersResponse

    @GET("api/my-requests/{room_no}")
    suspend fun getMyRequests(@Path("room_no") roomNo: Int): List<UnifiedRequest>

    @FormUrlEncoded
    @POST("send-message")
    suspend fun sendMessage(
        @Field("room_no") roomNo: Int,
        @Field("message") message: String
    ): Response<Unit>
}
