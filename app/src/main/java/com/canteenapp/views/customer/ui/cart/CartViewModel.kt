package com.canteenapp.views.customer.ui.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.canteenapp.data.OrderItem
import com.canteenapp.repository.FirebaseDatabaseRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class CartViewModel : ViewModel() {

    private val database = FirebaseDatabaseRepository.getInstance()
    private val orders = MutableLiveData<List<OrderItem>>()

    fun getItems() :LiveData<List<OrderItem>> {
        database.orderRef
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val list = mutableListOf<OrderItem>()
                        for ( orderItem in snapshot.children ) {
                            val item: Map<*, *> = orderItem.value as Map<*, *>
                            list.add(OrderItem(
                                orderId = item["orderId"] as String,
                                price = (item["price"] as Long).toFloat(),
                                isDelivered = item["delivered"] as Boolean
                            ))
                        }
                        orders.value = list
                    }

                    override fun onCancelled(error: DatabaseError)  = Unit
                })
        return orders
    }
}