package com.example.joymap.Models

data class SafeZone(
    val id: String,
    val name: String,
    val color: Int,
    val location: String,
    val radius: Float,
    val ownerId: String?
)

