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
    ): String = withContext(Dispatchers.IO) {

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

        return@withContext try {

            val response = connection.inputStream.bufferedReader().readText()

            connection.disconnect()

            response // ESP32 "OK" döndürmeli

        } catch (e: Exception) {
            connection.disconnect()
            "ERROR"
        }
    }

    suspend fun ping(): Boolean = withContext(Dispatchers.IO) {

        return@withContext try {
            val url = URL("$baseUrl/ping")

            val connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 2000
            connection.readTimeout = 2000

            val code = connection.responseCode
            connection.disconnect()

            code == 200

        } catch (e: Exception) {
            false
        }
    }
}
