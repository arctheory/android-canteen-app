package com.canteenapp.views

import android.content.DialogInterface
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.canteenapp.R
import com.canteenapp.repository.FirebaseAuthRepository
import kotlinx.coroutines.launch

class ForgotActivity : AppCompatActivity() {

    private val authService = FirebaseAuthRepository.getInstance()
    private val emailInput by lazy {
        findViewById<EditText>(R.id.email)
    }

    private val loading by lazy {
        findViewById<ProgressBar>(R.id.loading)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot)

        findViewById<ImageButton>(R.id.forgot_back).setOnClickListener { finish() }

        findViewById<Button>(R.id.send_btn).setOnClickListener {
            val button = it
            val email = emailInput.text.toString()
            if ( email !== "" ) {
                lifecycleScope.launch {
                    loading.visibility = ProgressBar.VISIBLE
                    button.visibility = Button.GONE
                    authService.resetPassword(email).let {
                        loading.visibility = ProgressBar.GONE
                        button.visibility = Button.VISIBLE
                        if ( it ) {
                            showAlert("Sent reset link to your email!") {
                                finish()
                            }
                        } else {
                            showAlert("Unable to send") {}
                        }
                    }
                }
            }
        }
    }

    private fun showAlert(msg: String, done: () -> Unit) {
        AlertDialog.Builder(this).apply {
            title = "Reset"
            setMessage(msg)
            setPositiveButton("Ok", (DialogInterface.OnClickListener { _, _ -> done() }))
        }.run {
            val alertDialog = this.create()
            alertDialog.show()
        }
    }
}