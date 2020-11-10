package com.canteenapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.canteenapp.repository.FirebaseAuthRepository
import com.canteenapp.views.LoginActivity
import com.canteenapp.views.customer.CustomerHome

class SplashActivity : AppCompatActivity() {

    private val authState by lazy {
        FirebaseAuthRepository.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val openIntent = if ( authState.getUser() !== null ) {
            Intent(this, CustomerHome::class.java)
        } else {
            Intent(this, LoginActivity::class.java)
        }

        openIntent.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }.run {
            startActivity(this)
        }
    }
}