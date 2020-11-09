package com.storeapp.views

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuthException
import com.storeapp.R
import com.storeapp.repository.FirebaseAuthRepository
import com.storeapp.views.customer.CustomerHome
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private var authService = FirebaseAuthRepository.getInstance()
    private var email: String = ""
    private var password: String = ""
    private val loginButton by lazy {
        findViewById<Button>(R.id.login_btn)
    }
    private val loading by lazy {
        findViewById<ProgressBar>(R.id.loading)
    }
    private val signUpButton by lazy {
        findViewById<Button>(R.id.sign_up_btn)
    }
    private val forgotButton by lazy {
        findViewById<TextView>(R.id.forgot_btn)
    }
    private var isLoading: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if ( authService.getUser() != null ) {
            gotoHome()
        }

        findViewById<EditText>(R.id.email).doOnTextChanged { text, _, _, _ ->
            email = text.toString()
        }
        findViewById<EditText>(R.id.password).doOnTextChanged { text, _, _, _ ->
            password = text.toString()
        }

        loginButton.setOnClickListener {
            launchSignInFlow()
        }

        signUpButton.setOnClickListener {
            Intent(this, SignUpActivity::class.java).run {
                startActivity(this)
            }
        }

        forgotButton.setOnClickListener {
            Intent(this, ForgotActivity::class.java).run {
                startActivity(this)
            }
        }

    }

    private fun gotoHome() {
        Intent(this, CustomerHome::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }.run {
            startActivity(this)
        }
    }

    private fun launchSignInFlow() {
        if ( email !== "" && password !== "" ) {
            toggleLoading()
            this.lifecycleScope.launch {
                try {
                    authService.login(email, password)?.let {
                        gotoHome()
                    } ?: run {
                        toggleLoading()
                        showAlert("Check your network connection")
                    }
                } catch (e: FirebaseAuthException) {
                    toggleLoading()
                    when(e.errorCode.toString()) {
                        "ERROR_USER_NOT_FOUND" -> {
                            showAlert("User with provided email not found. Please sign up and try again")
                        }
                        else -> {
                            showAlert(e.message.toString())
                        }
                    }
                }
            }
        }
    }

    private fun toggleLoading() {
        if ( isLoading ) {
            loading.visibility = ProgressBar.GONE
            loginButton.visibility = Button.VISIBLE
        } else {
            loginButton.visibility = Button.GONE
            loading.visibility = ProgressBar.VISIBLE
        }
        isLoading = !isLoading
    }

    private fun showAlert(msg: String) {
        AlertDialog.Builder(this).apply {
            title = "Login Failed"
            setMessage(msg)
            setPositiveButton("Ok", null)
        }.run {
            val alertDialog = this.create()
            alertDialog.show()
        }
    }
}