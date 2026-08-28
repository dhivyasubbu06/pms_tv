package com.pinehotel.hospitality.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pinehotel.hospitality.network.*
import com.pinehotel.hospitality.utils.PreferenceManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class CartItem(
    val id: Int,
    val title: String,
    val price: Double,
    val quantity: Int,
    val type: String,
    val slot: String? = null
) {
    val cartKey: String get() = if (slot != null) "${type}_${id}_$slot" else "${type}_$id"
}

data class CartState(
    val totalCount: Int = 0,
    val totalPrice: Double = 0.0,
    val itemsMap: Map<String, CartItem> = emptyMap()
)

data class OrderResult(
    val serviceType: String,
    val success: Boolean,
    val message: String?,
    val orderId: String? = null
)

class CartViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = GuestRepository(RetrofitClient.apiService)
    private val preferenceManager = PreferenceManager(application)

    private val _cartState = MutableStateFlow(CartState())
    val cartState: StateFlow<CartState> = _cartState.asStateFlow()

    private val _submissionState = MutableStateFlow<List<OrderResult>>(emptyList())
    val submissionState: StateFlow<List<OrderResult>> = _submissionState.asStateFlow()

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    fun clearCart() {
        _cartState.value = CartState()
    }

    fun clearSubmissionState() {
        _submissionState.value = emptyList()
    }

    suspend fun getRoomNumber(): String? {
        return preferenceManager.roomNumberFlow.first()
    }

    fun placeOrder() {
        viewModelScope.launch {
            _isSubmitting.value = true
            val currentState = _cartState.value
            val rawRoom = preferenceManager.roomNumberFlow.first() ?: "0"
            val roomNo = rawRoom.replace(Regex("[^0-9]"), "").toIntOrNull() ?: 0
            
            val groupedItems = currentState.itemsMap.values.groupBy { it.type }
            val results = mutableListOf<OrderResult>()

            groupedItems.forEach { (type, items) ->
                val request = OrderRequest(
                    roomNo = roomNo,
                    serviceType = type,
                    items = items.map { OrderRequestItem(it.id, it.title, it.quantity, it.price.toInt(), it.slot) },
                    total = items.sumOf { it.price * it.quantity }.toInt()
                )

                repository.submitOrder(request)
                    .onSuccess { response ->
                        if (response.isSuccess) {
                            Log.d("CartViewModel", "Order success for $type: ${response.displayMessage}")
                            results.add(OrderResult(type, true, response.displayMessage, "#${(1000..9999).random()}"))
                        } else {
                            Log.e("CartViewModel", "Server failure for $type: ${response.displayMessage}")
                            results.add(OrderResult(type, false, response.displayMessage))
                        }
                    }
                    .onFailure { exception ->
                        Log.e("CartViewModel", "Network/Request failed for $type", exception)
                        val errorMsg = when (exception) {
                            is java.net.ConnectException -> "Cannot connect to server. Check backend status."
                            is retrofit2.HttpException -> "Server error ${exception.code()}: ${exception.message()}"
                            else -> exception.message ?: "Unknown network error"
                        }
                        results.add(OrderResult(type, false, errorMsg))
                    }
            }

            _submissionState.value = results
            _isSubmitting.value = false
        }
    }


    fun addItem(item: CartItem) {
        _cartState.update { currentState ->
            val updatedMap = currentState.itemsMap.toMutableMap()
            val existingItem = updatedMap[item.cartKey]
            val isBooking = item.type in listOf("spa", "reservation", "transport", "activity")
            
            if (existingItem != null) {
                if (isBooking) {
                    // For bookings, don't increment, just keep it at 1
                    updatedMap[item.cartKey] = existingItem.copy(quantity = 1)
                } else {
                    updatedMap[item.cartKey] = existingItem.copy(quantity = existingItem.quantity + item.quantity)
                }
            } else {
                updatedMap[item.cartKey] = item
            }
            calculateState(updatedMap)
        }
    }

    fun removeItem(item: CartItem) {
        _cartState.update { currentState ->
            val updatedMap = currentState.itemsMap.toMutableMap()
            updatedMap.remove(item.cartKey)
            calculateState(updatedMap)
        }
    }

    fun updateQuantity(item: CartItem, delta: Int) {
        _cartState.update { currentState ->
            val updatedMap = currentState.itemsMap.toMutableMap()
            val existingItem = updatedMap[item.cartKey]
            if (existingItem != null) {
                val newQuantity = existingItem.quantity + delta
                if (newQuantity > 0) {
                    updatedMap[item.cartKey] = existingItem.copy(quantity = newQuantity)
                } else {
                    updatedMap.remove(item.cartKey)
                }
            }
            calculateState(updatedMap)
        }
    }

    fun getQuantity(item: CartItem): Int = _cartState.value.itemsMap[item.cartKey]?.quantity ?: 0

    private fun calculateState(itemsMap: Map<String, CartItem>): CartState {
        val totalCount = itemsMap.values.sumOf { it.quantity }
        val totalPrice = itemsMap.values.sumOf { it.price * it.quantity }
        return CartState(totalCount, totalPrice, itemsMap)
    }
}
