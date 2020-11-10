package com.canteenapp.views.customer.ui.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.canteenapp.data.Customer
import com.canteenapp.repository.FirebaseDatabaseRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class AccountViewModel : ViewModel() {

    private val database = FirebaseDatabaseRepository.getInstance()
    private val _profile = MutableLiveData<Customer>().apply {
        value = Customer("", "")
    }
    val profile = _profile

    fun getProfile() : LiveData<Customer> {
        database.profile
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val customer: Map<*, *> = snapshot.value as Map<*, *>
                        profile.value = Customer(
                            customer["name"] as String,
                            customer["email"] as String,
                        )
                    }

                    override fun onCancelled(error: DatabaseError) = Unit
                })
        return profile
    }
}