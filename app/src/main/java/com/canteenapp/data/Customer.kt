package com.canteenapp.data

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Customer(
    val name: String,
    val email: String,
)