package com.example.joymap.Models.Requests

data class ChildLinkRequest(
    val parentId: String,
    val code1: String,
    val code2: String
)