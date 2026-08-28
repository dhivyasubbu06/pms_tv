package com.pinehotel.hospitality.models

data class BookingUIModel(
    val id: Int? = 0,
    val title: String? = "",
    val price: Double? = 0.0,
    val imageUrl: String? = "",
    val slots: List<String> = emptyList(),
    val type: String // "spa", "reservation", "transport", "activity"
)
