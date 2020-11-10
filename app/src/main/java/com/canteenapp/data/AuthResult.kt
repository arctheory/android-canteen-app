package com.canteenapp.data

import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseUser

data class AuthResult(
    val user: FirebaseUser?,
    val exception: FirebaseException?
)