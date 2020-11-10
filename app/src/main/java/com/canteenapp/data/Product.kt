package com.canteenapp.data

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Product(
    val imgUrl: String,
    val name: String,
    val price: Float,
)