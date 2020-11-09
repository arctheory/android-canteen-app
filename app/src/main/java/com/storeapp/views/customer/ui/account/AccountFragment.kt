package com.storeapp.views.customer.ui.account

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.storeapp.R
import com.storeapp.repository.FirebaseAuthRepository
import com.storeapp.views.LoginActivity
import kotlinx.coroutines.launch

class AccountFragment : Fragment() {

    private var authService = FirebaseAuthRepository.getInstance()
    private lateinit var accountViewModel: AccountViewModel
    private lateinit var profileName: TextView
    private lateinit var profileEmail: TextView
    private lateinit var profileAddressInput: EditText
    private lateinit var saveButton: Button
    private lateinit var logoutButton: Button
    private lateinit var loading: ProgressBar

    private var isLoading = false
    private var address = ""
    private var newAddress= ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        accountViewModel =
            ViewModelProvider(this).get(AccountViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_account, container, false)
        profileName = root.findViewById(R.id.profile_name)
        profileEmail = root.findViewById(R.id.profile_email)
        profileAddressInput = root.findViewById(R.id.profile_address)

        saveButton = root.findViewById(R.id.save_btn)
        saveButton.setOnClickListener { launchSaveAddress() }

        logoutButton = root.findViewById(R.id.logout_btn)
        logoutButton.setOnClickListener {
            lifecycleScope.launch {
                authService.logout().let {
                    if ( it ) {
                        Intent(root.context, LoginActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }.run {
                            startActivity(this)
                        }

                    } else {
                        AlertDialog.Builder(root.context).apply {
                            setTitle("Logout")
                            setMessage("Failed to logout")
                            setPositiveButton("Ok", null)
                        }.run {
                            this.create().run { show() }
                        }
                    }
                }
            }
        }
        loading = root.findViewById(R.id.loading)

        setDataObserver()

        profileAddressInput.doOnTextChanged { text, _, _, _ ->
            if ( newAddress !== address )
                showSaveButton()
            newAddress = text.toString()

            if ( text.toString() === address )
                hideSaveButton()
        }
        return root
    }

    private fun launchSaveAddress() {
        lifecycleScope.launch {
            toggleLoading()
            accountViewModel.updateAddress(newAddress).let {
                toggleLoading()
                newAddress = ""
                if ( !it ) {
                    showSaveButton()
                    showAlert("Unable to update address. Try again!")
                }
            }
        }
    }

    private fun showSaveButton() {
        saveButton.visibility = Button.VISIBLE
    }

    private fun hideSaveButton() {
        saveButton.visibility = Button.GONE
    }

    private fun toggleLoading() {
        if ( !isLoading ) {
            if ( saveButton.visibility == Button.VISIBLE ) saveButton.visibility = Button.GONE
            loading.visibility = ProgressBar.VISIBLE
        } else {
            loading.visibility = ProgressBar.GONE
        }
        isLoading = !isLoading
    }

    override fun onResume() {
        super.onResume()
        setDataObserver()
    }

    private fun setDataObserver() {
        accountViewModel.getProfile().observe(viewLifecycleOwner, {
            profileName.text = it.name
            profileEmail.text = it.email
            address = it.address
            newAddress = it.address
            profileAddressInput.setText(it.address)
        })
    }

    private fun showAlert(msg: String) {
        AlertDialog.Builder(this.requireContext()).apply {
            setTitle("Update Address")
            setMessage(msg)
            setPositiveButton("Ok", null)
        }.run {
            val alertDialog = this.create()
            alertDialog.show()
        }
    }
}