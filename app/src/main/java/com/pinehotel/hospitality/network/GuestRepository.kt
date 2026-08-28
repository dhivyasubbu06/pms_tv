package com.pinehotel.hospitality.network

class GuestRepository(private val apiService: ApiService) {
    suspend fun getGuestInfo(): Result<List<GuestInfo>> {
        return try {
            val response = apiService.getGuestInfo()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRoomServiceItems(): Result<List<ServiceItem>> {
        return try {
            val response = apiService.getRoomServiceItems()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getServices(): Result<List<ServiceTile>> {
        return try {
            val response = apiService.getServices()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getFoodItems(category: String? = null): Result<List<FoodMenuItem>> {
        return try {
            val response = apiService.getFoodItems(category)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSpaItems(category: String? = null): Result<List<SpaServiceItem>> {
        return try {
            val response = apiService.getSpaItems(category)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getEntertainmentItems(category: String? = "all"): Result<List<SpaServiceItem>> {
        return try {
            val response = apiService.getEntertainmentItems(category)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }



    suspend fun submitEntertainmentBooking(roomNo: Int, itemId: Int, title: String, slot: String): Result<ServiceResponse> {
        return try {
            val request = EntertainmentBookingRequest(
                roomNo = roomNo,
                itemId = itemId,
                itemTitle = title,
                slot = slot,
                guests = 1
            )
            val response = apiService.submitEntertainmentBooking(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun submitActivityBooking(roomNo: Int, activityId: Int, title: String, slot: String): Result<ServiceResponse> {
        return try {
            val request = ActivityBookingRequest(
                roomNo = roomNo,
                activityId = activityId,
                title = title,
                timeSlot = slot
            )
            val response = apiService.submitActivityBooking(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun submitBooking(roomNo: Int, request: GenericBookingRequest): Result<ServiceResponse> {
        return try {
            val response = apiService.submitBooking(roomNo, request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getActivities(category: String? = null): Result<List<Activity>> {
        return try {
            val response = apiService.getActivities(category)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getDineItems(occasion: String? = null): Result<List<DineItem>> {
        return try {
            val response = apiService.getDineItems(occasion)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getBarItems(category: String? = null): Result<List<BarItem>> {
        return try {
            val response = apiService.getBarItems(category)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTransportItems(): Result<List<BarItem>> {
        return try {
            val response = apiService.getTransportItems()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getLaundryItems(): Result<List<BarItem>> {
        return try {
            val response = apiService.getLaundryItems()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getShopItems(): Result<List<BarItem>> {
        return try {
            val response = apiService.getShopItems()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun submitOrder(request: OrderRequest): Result<ServiceResponse> {
        return try {
            val response = apiService.submitOrder(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun submitHousekeepingRequest(request: HousekeepingRequest): Result<ServiceResponse> {
        return try {
            val response = apiService.submitHousekeepingRequest(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun submitSpaBooking(request: SpaBookingRequest): Result<ServiceResponse> {
        return try {
            val response = apiService.submitSpaBooking(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun submitDineBooking(request: DineBookingRequest): Result<ServiceResponse> {
        return try {
            val response = apiService.submitDineBooking(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun submitRestaurantReservation(request: RestaurantReservationRequest): Result<ServiceResponse> {
        // Redundant, but keeping for compatibility if needed. Backend uses /api/dine-booking
        return try {
            val response = apiService.submitDineBooking(DineBookingRequest(request.roomNo, 0, request.restaurantName, "General", request.slot))
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun submitActivityBooking(request: ActivityBookingRequest): Result<ServiceResponse> {
        return try {
            val response = apiService.submitActivityBooking(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun submitTransportRequest(request: TransportRequest): Result<ServiceResponse> {
        return try {
            val response = apiService.submitTransportRequest(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun submitLaundryRequest(request: LaundryRequest): Result<ServiceResponse> {
        return try {
            val response = apiService.submitLaundryRequest(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun submitConciergeRequest(request: ConciergeRequest): Result<ServiceResponse> {
        return try {
            val response = apiService.submitConciergeRequest(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun submitMaintenanceRequest(request: MaintenanceRequest): Result<ServiceResponse> {
        return try {
            val response = apiService.submitMaintenanceRequest(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun submitShopOrder(request: ShopOrderRequest): Result<ServiceResponse> {
        return try {
            val response = apiService.submitShopOrder(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMyOrdersFull(roomNo: Int): Result<MyOrdersResponse> {
        return try {
            val response = apiService.getMyOrders(roomNo)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUnifiedRequests(roomNo: Int): Result<List<UnifiedRequest>> {
        return try {
            val response = apiService.getMyRequests(roomNo)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMyRequests(roomNo: Int): Result<List<GuestOrder>> {
        return try {
            val response = apiService.getMyOrders(roomNo)
            val allOrders = mutableListOf<GuestOrder>()
            allOrders.addAll(response.orders)
            response.foodOrders?.let { allOrders.addAll(it) }
            response.barOrders?.let { allOrders.addAll(it) }
            response.spaBookings?.let { allOrders.addAll(it) }
            response.entertainmentBookings?.let { allOrders.addAll(it) }
            response.activityBookings?.let { allOrders.addAll(it) }
            response.dineBookings?.let { allOrders.addAll(it) }
            Result.success(allOrders)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendMessage(roomNo: Int, message: String): Result<Unit> {
        return try {
            val response = apiService.sendMessage(roomNo, message)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
