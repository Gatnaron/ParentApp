package com.example.joymap.Models

data class Child(
    val id: String,
    val deviceId: String,
    val location: String,
    val parentId: String,
    val timestamp: String,
    val battery: Int
)
