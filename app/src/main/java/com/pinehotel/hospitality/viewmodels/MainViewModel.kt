package com.pinehotel.hospitality.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pinehotel.hospitality.network.*
import com.pinehotel.hospitality.utils.PreferenceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = GuestRepository(RetrofitClient.apiService)
    private val preferenceManager = PreferenceManager(application)

    private val _guestInfo = MutableStateFlow<GuestInfo?>(null)
    val guestInfo: StateFlow<GuestInfo?> = _guestInfo.asStateFlow()

    private val _myOrders = MutableStateFlow<List<GuestOrder>>(emptyList())
    val myOrders: StateFlow<List<GuestOrder>> = _myOrders.asStateFlow()

    private val _serviceItems = MutableStateFlow<List<ServiceItem>>(emptyList())
    val serviceItems: StateFlow<List<ServiceItem>> = _serviceItems.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _requestSuccess = MutableStateFlow<Boolean?>(null)
    val requestSuccess: StateFlow<Boolean?> = _requestSuccess.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private fun parseRoomNumber(raw: String?): Int? {
        if (raw == null) return null
        return raw.replace(Regex("[^0-9]"), "").toIntOrNull()
    }

    fun fetchGuestInfo(myRoomNo: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repository.getGuestInfo()
                .onSuccess { list ->
                    val matched = list.find { it.roomNo == myRoomNo }
                    if (matched != null) {
                        _guestInfo.value = matched
                    } else {
                        _guestInfo.value = null
                        _error.value = "Room $myRoomNo not found in current guests"
                    }
                }
                .onFailure { _error.value = "Failed to fetch guest info: ${it.message}" }
            _isLoading.value = false
        }
    }

    fun fetchGuestInfo() {
        viewModelScope.launch {
            val rawRoom = preferenceManager.roomNumberFlow.first()
            val roomNo = parseRoomNumber(rawRoom)
            if (roomNo != null) fetchGuestInfo(roomNo)
        }
    }

    fun fetchRoomServiceItems() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repository.getRoomServiceItems()
                .onSuccess { _serviceItems.value = it }
                .onFailure { _error.value = "Failed to fetch items: ${it.message}" }
            _isLoading.value = false
        }
    }

    fun fetchMyOrders() {
        viewModelScope.launch {
            _isLoading.value = true
            val rawRoom = preferenceManager.roomNumberFlow.first()
            val roomNo = parseRoomNumber(rawRoom)
            if (roomNo != null) {
                repository.getMyRequests(roomNo)
                    .onSuccess { _myOrders.value = it }
                    .onFailure { _error.value = "Failed to fetch orders: ${it.message}" }
            } else {
                _error.value = "Room number not set. Please setup station."
            }
            _isLoading.value = false
        }
    }

    private var trackingJob: kotlinx.coroutines.Job? = null
    fun startOrderTracking() {
        trackingJob?.cancel()
        trackingJob = viewModelScope.launch {
            while (true) {
                val rawRoom = preferenceManager.roomNumberFlow.first()
                val roomNo = parseRoomNumber(rawRoom)
                if (roomNo != null) {
                    repository.getMyRequests(roomNo)
                        .onSuccess { _myOrders.value = it }
                }
                kotlinx.coroutines.delay(5000)
            }
        }
    }

    fun submitServiceRequest(type: String, serviceId: Int, serviceTitle: String, note: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _requestSuccess.value = null
            val rawRoom = preferenceManager.roomNumberFlow.first()
            val roomNo = parseRoomNumber(rawRoom)
            
            if (roomNo != null) {
                val result = when (type.lowercase()) {
                    "housekeeping" -> repository.submitHousekeepingRequest(
                        HousekeepingRequest(roomNo, serviceId, serviceTitle, note ?: "")
                    )
                    "spa" -> repository.submitSpaBooking(
                        SpaBookingRequest(roomNo, serviceId, serviceTitle, "General", note ?: "ASAP", 0)
                    )
                    "restaurant", "reservation" -> repository.submitDineBooking(
                        DineBookingRequest(roomNo, serviceId, serviceTitle, "General", note ?: "ASAP")
                    )
                    "activity" -> repository.submitActivityBooking(
                        roomNo, serviceId, serviceTitle, note ?: "ASAP"
                    )
                    "entertainment" -> repository.submitEntertainmentBooking(
                        roomNo, serviceId, serviceTitle, note ?: "ASAP"
                    )
                    "transport" -> repository.submitTransportRequest(
                        TransportRequest(roomNo, serviceId, serviceTitle, note ?: "ASAP", "")
                    )
                    "laundry" -> repository.submitLaundryRequest(
                        LaundryRequest(roomNo, serviceId, serviceTitle, 0, note ?: "")
                    )
                    "concierge" -> repository.submitConciergeRequest(
                        ConciergeRequest(roomNo, serviceTitle, note ?: "")
                    )
                    "maintenance" -> repository.submitMaintenanceRequest(
                        MaintenanceRequest(roomNo, serviceTitle, note ?: "")
                    )
                    "shop" -> repository.submitShopOrder(
                        ShopOrderRequest(roomNo, serviceId, serviceTitle, 0, 1)
                    )
                    else -> Result.failure(Exception("Unknown service type: $type"))
                }

                result.onSuccess {
                    if (it.isSuccess) {
                        _requestSuccess.value = true
                        fetchMyOrders() 
                        // Note: If you want structured refresh here, we can trigger it.
                    } else {
                        _error.value = it.displayMessage
                        _requestSuccess.value = false
                    }
                }.onFailure {
                    _error.value = "Failed to send request: ${it.message}"
                    _requestSuccess.value = false
                }
            } else {
                _error.value = "Device room number not found. Please setup station."
                _requestSuccess.value = false
            }
            _isLoading.value = false
        }
    }

    fun resetRequestState() {
        _requestSuccess.value = null
        _error.value = null
    }
}
