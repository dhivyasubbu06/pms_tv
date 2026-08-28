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

class BookingViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = GuestRepository(RetrofitClient.apiService)
    private val preferenceManager = PreferenceManager(application)

    private val _bookingData = MutableStateFlow<MyOrdersResponse?>(null)
    val bookingData: StateFlow<MyOrdersResponse?> = _bookingData.asStateFlow()

    private val _requestsList = MutableStateFlow<List<UnifiedRequest>>(emptyList())
    val requestsList: StateFlow<List<UnifiedRequest>> = _requestsList.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private var pollingJob: kotlinx.coroutines.Job? = null

    fun fetchBookings() {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (true) {
                _isLoading.value = _bookingData.value == null // Only show loader for first fetch
                _error.value = null
                
                val rawRoom = preferenceManager.roomNumberFlow.first()
                val roomNo = rawRoom?.replace(Regex("[^0-9]"), "")?.toIntOrNull()
                
                if (roomNo != null) {
                    // Fetch structured totals and meal plan
                    repository.getMyOrdersFull(roomNo)
                        .onSuccess { _bookingData.value = it }
                        .onFailure { _error.value = "Failed to fetch totals: ${it.message}" }
                } else {
                    _error.value = "Room number not set."
                }
                _isLoading.value = false
                
                // Refresh every 5 seconds for "instant" update feel matching website
                kotlinx.coroutines.delay(5000)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        pollingJob?.cancel()
    }
}
