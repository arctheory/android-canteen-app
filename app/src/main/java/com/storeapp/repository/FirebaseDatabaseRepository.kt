package com.storeapp.repository

import com.google.firebase.FirebaseException
import com.google.firebase.database.FirebaseDatabase
import com.storeapp.data.BagItem
import com.storeapp.data.Customer
import com.storeapp.data.Product
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import java.sql.Timestamp

class FirebaseDatabaseRepository {
    private val authInstance = FirebaseAuthRepository.getInstance()
    private val firebaseDatabase by lazy {
        FirebaseDatabase.getInstance()
    }

    val profile = firebaseDatabase.getReference(authInstance.getUser()!!.uid)
    val bag = firebaseDatabase.getReference("bag/${authInstance.getUser()!!.uid}")
    val productRef = firebaseDatabase.getReference("products")
    val orderRef = firebaseDatabase.getReference("orders/${authInstance.getUser()!!.uid}")

    suspend fun addToBag(product: Product): Boolean = coroutineScope {
        val profileFetch = async<Customer?> {
            authInstance.getProfile()
        }
        val profile = profileFetch.await() ?: return@coroutineScope false

        val added = async<Boolean> {
            try {
                val newItem = BagItem(
                    profile,
                    product,
                    Timestamp(System.currentTimeMillis()).time.toString()
                )
                val newData = bag.push()
                newData.setValue(newItem).await()
                true
            } catch (e: FirebaseException) {
                false
            }
        }
        added.await()
    }

    suspend fun saveNewAddress(address: String): Boolean = coroutineScope {
        val task = async<Boolean> {
            try {
                val taskMap = HashMap<String, Any>()
                taskMap["address"] = address
                profile.updateChildren(taskMap).await()
                true
            } catch (e: FirebaseException) {
                false
            }
        }
        task.await()
    }

    companion object {
        private var appDatabase: FirebaseDatabaseRepository? = null

        fun getInstance(): FirebaseDatabaseRepository {
            if ( appDatabase == null )
                appDatabase = FirebaseDatabaseRepository()
            return appDatabase!!
        }
    }
}