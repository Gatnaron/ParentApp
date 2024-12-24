package com.example.joymap.Services

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.joymap.Models.Requests.ChildLinkRequest
import com.example.joymap.Models.Requests.SafeZoneRequest
import com.example.joymap.Models.SafeZone
import com.example.joymap.Models.User
import okhttp3.*
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.internal.http.HttpMethod
import java.io.IOException

class ParentApiService(private val context: Context) {
    // Базовый URL API
    private val BASE_URL = "http://10.0.2.2:5059/api/parent/"

    private val client = OkHttpClient()
    private val gson = Gson()

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("ParentAppPrefs", Context.MODE_PRIVATE)

    private fun saveGuidToPreferences(guid: String) {
        sharedPreferences.edit().putString("parentGuid", guid).apply()
    }

    fun createParent(deviceId: String, callback: (String) -> Unit) {
        val url = "${BASE_URL}create/$deviceId"

        val request = Request.Builder()
            .url(url)
            .post(RequestBody.create(null, ByteArray(0)))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val guid = response.body?.string() ?: ""
                    saveGuidToPreferences(guid)
                    callback("Parent created successfully: $guid")
                } else {
                    callback("Error: ${response.code}")
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                callback("Server error: ${e.message}")
            }
        })
    }

    // PUT запрос для обновления SafeZone
    // Функция для добавления безопасной зоны
    fun addSafeZone(ownerId: String, safeZoneRequest: SafeZoneRequest, callback: (String) -> Unit) {
        val url = "${BASE_URL}safezone"

        // Преобразование SafeZoneRequest в JSON с ownerId
        val requestBody = gson.toJson(
            mapOf(
                "ownerId" to ownerId,
                "name" to safeZoneRequest.name,
                "color" to safeZoneRequest.color,
                "location" to safeZoneRequest.location,
                "radius" to safeZoneRequest.radius
            )
        ).toRequestBody("application/json; charset=utf-8".toMediaType())

        // Создание PUT-запроса
        val request = Request.Builder()
            .url(url)
            .put(requestBody)
            .build()

        // Асинхронное выполнение запроса
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    response.body?.string()?.let { responseBody ->
                        Log.d("SafeZone", "Response: $responseBody")
                        callback(responseBody) // Возвращаем успешный ответ
                    } ?: callback("Error: Empty response body")
                } else {
                    Log.e("SafeZone", "Error: ${response.code}")
                    callback("Error: ${response.code}") // Возвращаем ошибочный код
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.e("SafeZone", "Error: ${e.message}")
                callback("Server error: ${e.message}") // Возвращаем сообщение об ошибке
            }
        })
    }

    fun linkChildToParent(childLink: ChildLinkRequest, callback: (String) -> Unit) {
        val url = "${BASE_URL}child"

        val requestBody = gson.toJson(childLink)
            .toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request.Builder()
            .url(url)
            .put(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let { callback(it) }
            }

            override fun onFailure(call: Call, e: IOException) {
                callback("Server error: ${e.message}")
            }
        })
    }

    fun getUserData(userId: String, callback: (User?, String?) -> Unit) {
        val url = "$BASE_URL$userId"
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let {
                    val user = gson.fromJson(it, User::class.java)
                    callback(user, null)
                } ?: callback(null, "ERROR GET DATA")
            }

            override fun onFailure(call: Call, e: IOException) {
                callback(null, "ERROR server: ${e.message}")
            }
        })
    }

    fun deleteSafeZone(zoneId: String, callback: (String) -> Unit) {
        val url = "${BASE_URL}safezone/$zoneId"
        val request = Request.Builder()
            .url(url)
            .delete()
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    callback("Safe zone deleted successfully")
                } else {
                    callback("Error: ${response.code}")
                }
            }
            override fun onFailure(call: Call, e: IOException) {
                callback("Server error: ${e.message}")
            }
        })
    }

}
