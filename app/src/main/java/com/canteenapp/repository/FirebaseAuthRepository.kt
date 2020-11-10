package com.canteenapp.repository

import com.canteenapp.data.AuthResult
import com.canteenapp.data.Customer
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository {

    private val database = FirebaseDatabase.getInstance()
    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    suspend fun createUser(newCustomer: Customer, password: String): AuthResult = coroutineScope {
        var exception: FirebaseException? = null
        val create = async<Boolean> {
            try {
                firebaseAuth.createUserWithEmailAndPassword(newCustomer.email, password).await()
                true
            } catch (e: FirebaseAuthException) {
                exception = e
                false
            }
        }
        val isSuccess = create.await()
        if ( isSuccess ) {

            val loginNew = async<FirebaseUser?> {
                firebaseAuth.signInWithEmailAndPassword(newCustomer.email, password).await()
                firebaseAuth.currentUser
            }
            val user = loginNew.await()

            val createProfile = async<Boolean> {
                try {
                    FirebaseDatabase.getInstance()
                        .reference.root
                        .child(user!!.uid)
                        .setValue(newCustomer)
                    true
                } catch (e: FirebaseException) {
                    exception = e
                    false
                }
            }
            val isProfileCreated = createProfile.await()

            if ( isProfileCreated ) {
                AuthResult(user, null)
            } else {
                user?.delete()?.await()
                AuthResult(null, exception)
            }
        } else {
            AuthResult(null, exception)
        }
    }

    suspend fun login(email: String, password: String): FirebaseUser? = coroutineScope {
        val login = async<FirebaseUser?> {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            firebaseAuth.currentUser
        }
        login.await()
    }

    fun getUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    suspend fun getProfile(): Customer? = coroutineScope {
        val profile = async<Customer?> {
            try {
                var profile: Customer? = null
                database.getReference(getUser()!!.uid)
                    .addListenerForSingleValueEvent(
                        object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val customer: Map<*, *> = snapshot.value as Map<*, *>
                                profile = Customer(
                                    customer["name"] as String,
                                    customer["email"] as String,
                                )
                            }

                            override fun onCancelled(error: DatabaseError) {
                                profile = Customer("","")
                            }
                        },
                    )
                while ( profile == null ) {
                    delay(16)
                    continue
                }
                if ( profile?.name == "" ) {
                    profile = null
                }
                profile
            } catch (e: Exception) {
                null
            }
        }
        profile.await()
    }

    suspend fun logout(): Boolean = coroutineScope {
        val logoutTask = async<Boolean> {
            try {
                firebaseAuth.signOut()
                true
            } catch (e: FirebaseAuthException) {
                false
            }
        }

        logoutTask.await()
    }

    suspend fun resetPassword(email: String): Boolean = coroutineScope {
        val reset = async<Boolean> {
            try {
                firebaseAuth.sendPasswordResetEmail(email).await()
                true
            } catch (e: FirebaseAuthException) {
                false
            }
        }
        reset.await()
    }

    companion object {
        private var authStore: FirebaseAuthRepository? = null

        fun getInstance(): FirebaseAuthRepository {
            if ( authStore == null )
                authStore = FirebaseAuthRepository()
            return authStore!!
        }
    }

}