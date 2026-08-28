package com.pinehotel.hospitality.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pinehotel.hospitality.R
import com.pinehotel.hospitality.network.*
import kotlinx.coroutines.launch

class ServicesViewModel : ViewModel() {

    private val repository = GuestRepository(RetrofitClient.apiService)

    private val _services = MutableLiveData<List<ServiceTile>>()
    val services: LiveData<List<ServiceTile>> = _services

    private val _foodItems = MutableLiveData<List<FoodMenuItem>>()
    val foodItems: LiveData<List<FoodMenuItem>> = _foodItems

    private val _spaItems = MutableLiveData<List<SpaServiceItem>>()
    val spaItems: LiveData<List<SpaServiceItem>> = _spaItems

    private val _activities = MutableLiveData<List<Activity>>()
    val activities: LiveData<List<Activity>> = _activities

    private val _dineItems = MutableLiveData<List<DineItem>>()
    val dineItems: LiveData<List<DineItem>> = _dineItems

    private val _barItems = MutableLiveData<List<BarItem>>()
    val barItems: LiveData<List<BarItem>> = _barItems

    // Temporarily restoring old LiveData to fix compilation in un-migrated fragments
    private val _laundryItems = MutableLiveData<List<com.pinehotel.hospitality.models.LaundryService>>()
    val laundryItems: LiveData<List<com.pinehotel.hospitality.models.LaundryService>> = _laundryItems

    private val _products = MutableLiveData<List<com.pinehotel.hospitality.models.ResortProduct>>()
    val products: LiveData<List<com.pinehotel.hospitality.models.ResortProduct>> = _products

    private val _maintenanceIssues = MutableLiveData<List<com.pinehotel.hospitality.models.MaintenanceIssue>>()
    val maintenanceIssues: LiveData<List<com.pinehotel.hospitality.models.MaintenanceIssue>> = _maintenanceIssues

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun fetchServices() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getServices()
                .onSuccess { list ->
                    // 1. Remove only clearly redundant/utility tiles, keeping main service categories
                    val filteredList = list.filterNot { 
                        val title = it.title.trim().lowercase()
                        title.equals("concierge", ignoreCase = true) ||
                        title.equals("my bookings", ignoreCase = true)
                    }
                    
                    // 2. Ensure essential services exist for the guest
                    var updatedList = filteredList
                    
                    val serviceChecks = listOf(
                        Triple("room service", "Room Service", "/static/images/services/room_service.jpg"),
                        Triple("housekeeping", "Housekeeping", "/static/images/services/housekeeping.jpg"),
                        Triple("transport", "Transportation", "/static/images/services/transport_cover.jpg"),
                        Triple("entertainment", "Entertainment", "/static/images/services/Entertainment.jpg"),
                        Triple("activity", "Activity Booking", "/static/images/services/activities.jpg"),
                        Triple("bar", "Bar & Lounge", "/static/images/services/bar.jpg"),
                        Triple("restaurant", "Restaurant Reservation", "/static/images/services/restaurant.jpg"),
                        Triple("spa", "Spa & Wellness", "/static/images/services/spa.jpg"),
                        Triple("laundry", "Laundry Service", "/static/images/services/laundry.jpg")
                    )

                    var currentId = 1000
                    serviceChecks.forEach { (keyword, display, defaultImg) ->
                        if (updatedList.none { it.title.contains(keyword, ignoreCase = true) }) {
                            updatedList = updatedList + ServiceTile(currentId++, display, defaultImg)
                        }
                    }

                    // 3. Robust Fix for images (handles broken/null URLs)
                    val finalServices = updatedList.map { service ->
                        val normalizedTitle = service.title.trim().lowercase()
                        val imageUrl = service.imageUrl
                        
                        // Check for missing, null-string, or default cloche images
                        val isImageMissing = imageUrl.isNullOrEmpty() || 
                                           imageUrl.trim().lowercase() == "null" || 
                                           imageUrl.contains("cloche", ignoreCase = true)
                        
                        when {
                            isImageMissing -> {
                                // Fallback for essential services
                                when {
                                    normalizedTitle.contains("room service") || normalizedTitle.contains("dining") -> service.copy(imageUrl = "/static/images/services/room_service.jpg")
                                    normalizedTitle.contains("housekeeping") -> service.copy(imageUrl = "/static/images/services/housekeeping.jpg")
                                    normalizedTitle.contains("transport") -> service.copy(imageUrl = "/static/images/services/transport_cover.jpg")
                                    normalizedTitle.contains("entertainment") -> service.copy(imageUrl = "/static/images/services/Entertainment.jpg")
                                    normalizedTitle.contains("activity") -> service.copy(imageUrl = "/static/images/services/activities.jpg")
                                    normalizedTitle.contains("bar") || normalizedTitle.contains("lounge") -> service.copy(imageUrl = "/static/images/services/bar.jpg")
                                    normalizedTitle.contains("restaurant") || normalizedTitle.contains("reservation") -> service.copy(imageUrl = "/static/images/services/restaurant.jpg")
                                    normalizedTitle.contains("spa") -> service.copy(imageUrl = "/static/images/services/spa.jpg")
                                    normalizedTitle.contains("laundry") -> service.copy(imageUrl = "/static/images/services/laundry.jpg")
                                    else -> service
                                }
                            }
                            else -> service
                        }
                    }
                    
                    _services.value = finalServices
                }
                .onFailure { _error.value = "Failed to load services" }
            _isLoading.value = false
        }
    }

    fun fetchFoodItems(category: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            val backendCategory = category?.lowercase()?.replace(" ", "_")
            repository.getFoodItems(backendCategory)
                .onSuccess { _foodItems.value = it }
                .onFailure { _error.value = "Failed to load food menu" }
            _isLoading.value = false
        }
    }

    fun fetchSpaItems(category: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getSpaItems(category?.lowercase())
                .onSuccess { _spaItems.value = it }
                .onFailure { _error.value = "Failed to load spa items" }
            _isLoading.value = false
        }
    }

    fun fetchActivities(category: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getActivities(category?.lowercase())
                .onSuccess { _activities.value = it }
                .onFailure { _error.value = "Failed to load activities" }
            _isLoading.value = false
        }
    }

    fun fetchDineItems(occasion: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getDineItems(occasion?.lowercase())
                .onSuccess { _dineItems.value = it }
                .onFailure { _error.value = "Failed to load dine items" }
            _isLoading.value = false
        }
    }

    fun fetchBarItems(category: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getBarItems(category?.lowercase())
                .onSuccess { _barItems.value = it }
                .onFailure { _error.value = "Failed to load bar items" }
            _isLoading.value = false
        }
    }

    private val _entertainmentItems = MutableLiveData<List<SpaServiceItem>>()
    val entertainmentItems: LiveData<List<SpaServiceItem>> = _entertainmentItems

    fun fetchEntertainmentItems(category: String? = null, silent: Boolean = false) {
        viewModelScope.launch {
            if (!silent) _isLoading.value = true
            
            // Fix: send lowercase category to backend
            val filter = category?.lowercase() ?: "all"
            
            repository.getEntertainmentItems(filter)
                .onSuccess { list ->
                    _entertainmentItems.value = list
                }
                .onFailure { 
                    _error.value = "Connect error, checking portal" 
                }
            if (!silent) _isLoading.value = false
        }
    }



    private val _activityBookingItems = MutableLiveData<List<SpaServiceItem>>()
    val activityBookingItems: LiveData<List<SpaServiceItem>> = _activityBookingItems

    fun fetchActivityBookingItems(category: String? = null, silent: Boolean = false) {
        // Redundant logic removed
    }

    private val _housekeepingItems = MutableLiveData<List<ServiceItem>>()
    val housekeepingItems: LiveData<List<ServiceItem>> = _housekeepingItems

    fun fetchHousekeepingItems() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getRoomServiceItems()
                .onSuccess { list ->
                    _housekeepingItems.value = list
                    if (list.isEmpty()) {
                        _error.value = "No housekeeping items in portal"
                    }
                }
                .onFailure { _error.value = "Connect error, check portal" }
            _isLoading.value = false
        }
    }

    private val _transportItems = MutableLiveData<List<BarItem>>()
    val transportItems: LiveData<List<BarItem>> = _transportItems

    fun fetchTransportItems(category: String? = null, silent: Boolean = false) {
        viewModelScope.launch {
            if (!silent) _isLoading.value = true
            repository.getTransportItems()
                .onSuccess { list ->
                    val filtered = when (category?.lowercase()) {
                        "airport" -> list.filter { it.title?.lowercase()?.contains("airport") == true }
                        "local" -> list.filter { it.title?.lowercase()?.contains("airport") == false }
                        else -> list
                    }
                    _transportItems.value = filtered
                    
                    if (list.isEmpty()) {
                        _error.value = "No transport items in portal"
                    }
                }
                .onFailure { _error.value = "Connect error, check portal" }
            if (!silent) _isLoading.value = false
        }
    }

    private val _dynLaundryItems = MutableLiveData<List<BarItem>>()
    val dynLaundryItems: LiveData<List<BarItem>> = _dynLaundryItems

    fun fetchLaundryItems() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getLaundryItems()
                .onSuccess { list ->
                    _dynLaundryItems.value = list
                    if (list.isEmpty()) {
                        _error.value = "No laundry items in portal"
                    }
                }
                .onFailure { _error.value = "Connect error, check portal" }
            _isLoading.value = false
        }
    }

    private val _dynShopItems = MutableLiveData<List<BarItem>>()
    val dynShopItems: LiveData<List<BarItem>> = _dynShopItems

    fun fetchShopItems() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getShopItems()
                .onSuccess { list ->
                    _dynShopItems.value = list
                    if (list.isEmpty()) {
                        _error.value = "No shop items in portal"
                    }
                }
                .onFailure { _error.value = "Connect error, check portal" }
            _isLoading.value = false
        }
    }

    fun getDestinationId(title: String): Int? {
        val normalized = title.trim().lowercase()
        
        return when {
            // Exact matches for your current Admin Panel tiles
            normalized.equals("in-room dining", ignoreCase = true) || normalized.equals("room service", ignoreCase = true) -> R.id.roomServiceFragment
            normalized.equals("lounge & bar", ignoreCase = true) || normalized.contains("bar", ignoreCase = true) || normalized.contains("lounge", ignoreCase = true) -> R.id.barFragment
            normalized.equals("fine dining", ignoreCase = true) || normalized.contains("restaurant", ignoreCase = true) || normalized.contains("reservation", ignoreCase = true) -> R.id.restaurantFragment
            normalized.equals("house keeping", ignoreCase = true) || normalized.contains("housekeeping", ignoreCase = true) -> R.id.housekeepingFragment
            normalized.equals("spa & wellness", ignoreCase = true) || normalized.contains("spa", ignoreCase = true) -> R.id.spaFragment
            normalized.equals("resort activities", ignoreCase = true) || normalized.contains("pool", ignoreCase = true) || normalized.contains("tour", ignoreCase = true) || normalized.contains("activity", ignoreCase = true) -> R.id.activityFragment
            normalized.equals("entertainment", ignoreCase = true) || normalized.contains("entertainment", ignoreCase = true) -> R.id.entertainmentFragment
            normalized.equals("my bookings & bills", ignoreCase = true) || normalized.contains("my requests", ignoreCase = true) -> R.id.myRequestsFragment
            normalized.equals("transportation", ignoreCase = true) || normalized.contains("transport", ignoreCase = true) -> R.id.transportationFragment
            normalized.equals("laundry service", ignoreCase = true) || normalized.contains("laundry", ignoreCase = true) -> R.id.laundryFragment
            
            // Keyword fallbacks just in case
            normalized.contains("dining", ignoreCase = true) && !normalized.contains("fine", ignoreCase = true) -> R.id.roomServiceFragment
            normalized.contains("cleaning", ignoreCase = true) -> R.id.housekeepingFragment
            normalized.contains("massage", ignoreCase = true) -> R.id.spaFragment
            normalized.contains("tour", ignoreCase = true) || normalized.contains("pool", ignoreCase = true) -> R.id.activityFragment
            normalized.contains("bill", ignoreCase = true) || normalized.contains("booking", ignoreCase = true) -> R.id.myRequestsFragment
            else -> null
        }
    }
}
