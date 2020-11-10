package com.canteenapp.views.customer.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.canteenapp.data.Product
import com.canteenapp.repository.FirebaseDatabaseRepository

class HomeViewModel : ViewModel() {

    private val database = FirebaseDatabaseRepository.getInstance()
    private val _products = MutableLiveData<List<Product>>().apply {
        value = listOf<Product>()
    }
    private var products = _products

    fun getProducts(): LiveData<List<Product>> {
        database.productRef.orderByChild("name").limitToFirst(32)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val itemsList = mutableListOf<Product>()
                    for ( productSnapshot in snapshot.children ) {
                        val item: Map<*, *> = productSnapshot.value as Map<*, *>
                        val product = Product(
                            item["imgUrl"] as String,
                            item["name"] as String,
                            (item["price"] as Long).toFloat(),
                        )
                        itemsList.add(product)
                    }
                    products.value = itemsList
                }
                override fun onCancelled(error: DatabaseError) = Unit
            })

        return  products
    }

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text
}