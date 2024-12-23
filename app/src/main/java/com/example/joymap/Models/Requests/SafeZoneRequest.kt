package com.example.joymap.Models.Requests

data class SafeZoneRequest(
    val name: String,
    val color: Int,
    val location: String,
    val radius: Float,
    val ownerId: String
)