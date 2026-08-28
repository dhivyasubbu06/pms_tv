package com.pinehotel.hospitality.utils

object UrlUtils {
    // For physical TVs and Emulator connection to PC Admin Portal
    private const val SERVER_IP = "192.168.0.173" 
    private const val BASE_URL = "http://$SERVER_IP:8001"

    fun getFullImageUrl(path: String?): String? {
        if (path.isNullOrEmpty()) return null
        
        val fullUrl = if (path.startsWith("http")) {
            // Unify all local IPs to the current server IP
            path.replace("localhost", SERVER_IP)
                .replace("127.0.0.1", SERVER_IP)
                .replace("10.0.2.2", SERVER_IP)
                .replace("10.60.252.87", SERVER_IP)
        } else {
            val normalizedPath = if (path.startsWith("/")) path else "/$path"
            "$BASE_URL$normalizedPath"
        }

        return fullUrl.replace(" ", "%20")
    }
}
