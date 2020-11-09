package com.storeapp.views.customer.ui.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.storeapp.data.Customer
import com.storeapp.repository.FirebaseDatabaseRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class AccountViewModel : ViewModel() {

    private val database = FirebaseDatabaseRepository.getInstance()
    private val _profile = MutableLiveData<Customer>().apply {
        value = Customer("", "", "")
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
                            customer["address"] as String
                        )
                    }

                    override fun onCancelled(error: DatabaseError) = Unit
                })
        return profile
    }

    suspend fun updateAddress(address: String): Boolean = coroutineScope {
        val update = async<Boolean> {
            val result = database.saveNewAddress(address)
            result
        }
        update.await()
    }
}