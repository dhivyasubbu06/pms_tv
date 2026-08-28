package com.pinehotel.hospitality.utils

object PriceUtils {
    fun getDynamicPrice(rawPrice: Double, type: String?, title: String?): Double {
        if (rawPrice > 0) return rawPrice
        
        val normalizedType = type?.lowercase() ?: ""
        val normalizedTitle = title?.lowercase() ?: ""
        
        return when {
            normalizedType == "spa" -> {
                when {
                    normalizedTitle.contains("massage") -> 3000.0
                    normalizedTitle.contains("facial") -> 2500.0
                    normalizedTitle.contains("body") -> 3500.0
                    else -> 2000.0
                }
            }
            normalizedType == "transport" -> {
                when {
                    normalizedTitle.contains("pickup") -> 1200.0
                    normalizedTitle.contains("drop") -> 1000.0
                    normalizedTitle.contains("taxi") -> 800.0
                    normalizedTitle.contains("shuttle") -> 500.0
                    else -> 600.0
                }
            }
            normalizedType == "entertainment" -> {
                when {
                    normalizedTitle.contains("tournament") -> 200.0
                    normalizedTitle.contains("movie") -> 300.0
                    normalizedTitle.contains("karaoke") -> 500.0
                    normalizedTitle.contains("water") -> 600.0
                    else -> 400.0
                }
            }
            normalizedType == "activity" -> {
                when {
                    normalizedTitle.contains("pool") -> 0.0 // Pool might be free
                    normalizedTitle.contains("tour") -> 1500.0
                    normalizedTitle.contains("class") -> 800.0
                    else -> 500.0
                }
            }
            normalizedType == "food" || normalizedType == "dining" -> {
                when {
                    normalizedTitle.contains("meal") -> 1200.0
                    normalizedTitle.contains("breakfast") -> 600.0
                    normalizedTitle.contains("dinner") -> 1500.0
                    else -> 450.0
                }
            }
            normalizedType == "bar" || normalizedType == "drink" -> {
                when {
                    normalizedTitle.contains("wine") -> 900.0
                    normalizedTitle.contains("cocktail") -> 700.0
                    normalizedTitle.contains("juice") -> 250.0
                    else -> 400.0
                }
            }
            else -> 100.0
        }
    }

    fun getDynamicPriceInt(rawPrice: Int, type: String?, title: String?): Int {
        return getDynamicPrice(rawPrice.toDouble(), type, title).toInt()
    }
}
