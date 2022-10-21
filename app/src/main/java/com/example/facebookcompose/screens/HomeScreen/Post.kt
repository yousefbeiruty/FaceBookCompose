package com.example.facebookcompose.screens.HomeScreen

import java.util.*

data class Post(
    val text: String,
    val timestamp: Date,
    val authorName: String,
    val authorAvatarUrl: String
)
