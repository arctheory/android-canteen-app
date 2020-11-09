package com.storeapp.views.customer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.storeapp.data.BagItem
import com.storeapp.data.Customer
import com.storeapp.data.OrderItem
import com.storeapp.data.Product
import com.storeapp.repository.FirebaseDatabaseRepository
import java.sql.Timestamp

class BagViewModel: ViewModel() {

    private val database = FirebaseDatabaseRepository.getInstance()
    private val bagItems = MutableLiveData<List<BagItem>>()

    fun getBagItems(): LiveData<List<BagItem>> {
        database.bag
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<BagItem>()
                    for ( item in snapshot.children ) {
                        val data: Map<*, *> = item.value as Map<*, *>
                        val customer: Map<*, *> = data["customer"] as Map<*, *>
                        val product: Map<*, *> = data["product"] as Map<*, *>
                        list.add(BagItem(
                            Customer(
                                name = customer["name"] as String,
                                email = customer["email"] as String,
                                address = customer["address"] as String
                            ),
                            Product(
                                imgUrl = product["imgUrl"] as String,
                                name = product["name"] as String,
                                price = (product["price"] as Long).toFloat(),
                            ),
                            data["timestamp"] as String
                        ))
                    }
                    bagItems.value = list
                }

                override fun onCancelled(error: DatabaseError) = Unit
            })

        return bagItems
    }

    fun placeOrder(callback: (Boolean) -> Unit) {
        val items = bagItems.value
        val order = OrderItem(
            orderId = Timestamp(System.currentTimeMillis()).time.toString(),
            productList = items!!.map { it.product },
            price = items.map { it.product.price }.reduce { acc, fl -> acc + fl }
        )
        database.orderRef.push().run {
            this.setValue(order)
        }
        database.bag
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.ref.removeValue()
                    callback(true)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false)
                }
            })
    }

    fun removeItem(timestamp: String) {
        database.bag.orderByChild("timestamp").equalTo(timestamp)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for ( item in snapshot.children ) {
                        item.ref.removeValue()
                    }
                }

                override fun onCancelled(error: DatabaseError) = Unit
            })
    }
}
