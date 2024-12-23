package com.example.joymap.Models

data class User(
    val id: String,
    val deviceId: String,
    val children: List<Child> = emptyList(),
    val safeZones: List<SafeZone> = emptyList()
)