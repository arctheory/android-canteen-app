package com.canteenapp.views

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import com.canteenapp.R
import com.canteenapp.data.Customer
import com.canteenapp.repository.FirebaseAuthRepository
import com.canteenapp.views.customer.CustomerHome
import kotlinx.coroutines.launch

class SignUpActivity : AppCompatActivity() {

    // Init FirebaseAuth instance from FirebaseAuthRepository
    private val authService = FirebaseAuthRepository.getInstance()

    // Init elements
    private val backButton by lazy {
        findViewById<ImageButton>(R.id.sign_up_back)
    }
    private val fullNameInput by lazy {
        findViewById<EditText>(R.id.full_name)
    }
    private val emailInput by lazy {
        findViewById<EditText>(R.id.email)
    }
    private val passwordInput by lazy {
        findViewById<EditText>(R.id.password)
    }
    private val signUpButton by lazy {
        findViewById<Button>(R.id.sign_up_btn)
    }
    private val loading by lazy {
        findViewById<ProgressBar>(R.id.loading)
    }

    private var isLoading = false

    // Sign up data
    private var fullName = ""
    private var email = ""
    private var password = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // Check if user is already logged in
        if ( authService.getUser() != null ) {
            gotoHome()
        }

        // Input field handlers
        fullNameInput.doOnTextChanged { text, _, _, _ -> fullName = text.toString() }
        emailInput.doOnTextChanged { text, _, _, _ -> email = text.toString() }
        passwordInput.doOnTextChanged { text, _, _, _ -> password = text.toString() }

        // Button click actions
        backButton.setOnClickListener { finish() }
        signUpButton.setOnClickListener {
            launchSignUpFlow()
        }

    }

    // Call createUser function from FirebaseAuthRepository
    private fun launchSignUpFlow() {
        if ( fullName !== "" && email !== "" && password !== "" ) {
            toggleLoading()
            lifecycleScope.launch {
                val result = authService.createUser(Customer(fullName, email), password)
                if ( result.user != null ) {
                    gotoHome()
                } else {
                    toggleLoading()
                    showAlert(result.exception?.message.toString())
                }
            }
        }
    }

    // Open CustomerHome activity
    private fun gotoHome() {
        Intent(this, CustomerHome::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }.run {
            startActivity(this)
        }
    }

    // Switch loading
    private fun toggleLoading() {
        if ( isLoading ) {
            loading.visibility = ProgressBar.GONE
            signUpButton.visibility = Button.VISIBLE
        } else {
            signUpButton.visibility = Button.GONE
            loading.visibility = ProgressBar.VISIBLE
        }
        isLoading = !isLoading
    }

    // Show resultant message
    private fun showAlert(msg: String) {
        AlertDialog.Builder(this).apply {
            title = "Sign Up Failed"
            setMessage(msg)
            setPositiveButton("Ok", null)
        }.run {
            val alertDialog = this.create()
            alertDialog.show()
        }
    }
}