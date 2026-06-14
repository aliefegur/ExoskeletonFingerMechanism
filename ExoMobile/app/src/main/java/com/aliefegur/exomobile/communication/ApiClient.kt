package com.aliefegur.exomobile.communication

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class ApiClient(private val baseUrl: String) {
    suspend fun sendMotionCommand(
        mode: String,
        duration: Float
    ): Boolean = withContext(Dispatchers.IO) {

        try {
            val url = URL("$baseUrl/move")
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json")
                doOutput = true
                connectTimeout = 3000
                readTimeout = 3000
            }

            val json = JSONObject().apply {
                put("mode", mode)
                put("duration", duration)
            }

            connection.outputStream.use {
                it.write(json.toString().toByteArray())
            }

            val code = connection.responseCode
            connection.disconnect()

            code in 200..299

        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
