package com.example.joymap.Services

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.util.Log

class LocationUpdateService : Service() {
    private lateinit var parentApiService: ParentApiService
    private val updateInterval = 30000L // 15 секунд
    private val handler = Handler()

    override fun onCreate() {
        super.onCreate()
        parentApiService = ParentApiService(this)
        startLocationUpdates()
    }

    private fun startLocationUpdates() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                updateChildrenLocations()
                handler.postDelayed(this, updateInterval)
            }
        }, updateInterval)
    }

    private fun updateChildrenLocations() {
        val sharedPreferences = getSharedPreferences("ParentAppPrefs", MODE_PRIVATE)
        val parentId = sharedPreferences.getString("parentGuid", "") ?: ""

        if (parentId.isNotEmpty()) {
            parentApiService.getUserData(parentId) { user, error ->
                if (user != null && user.children.isNotEmpty()) {
                    Log.i("LocationUpdate", "Обновлено положение детей")
                    for (child in user.children) {
                        val (latitude, longitude) = child.location.split(",").mapNotNull { it.toDoubleOrNull() }
                        Log.i("ChildLocation", "Имя: ${child.deviceId}, Координаты: $latitude, $longitude")
                    }
                } else {
                    Log.e("LocationUpdateError", "Ошибка обновления: $error")
                }
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}