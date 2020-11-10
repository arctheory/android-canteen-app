package com.canteenapp.views.customer.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.canteenapp.data.Product
import com.canteenapp.repository.FirebaseDatabaseRepository
import java.util.*

class SearchViewModel : ViewModel() {

    private val database = FirebaseDatabaseRepository.getInstance()
    private val _results = MutableLiveData<List<Product>>().apply {
        value = listOf<Product>()
    }
    private val results = _results

    fun getSearchResults(search: String?): LiveData<List<Product>> {
        if ( search !== null ) {
            database.productRef
                .orderByChild("name")
                .startAt(search.capitalize(Locale.ROOT))
                .endAt("${search}\uF8FF")
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
                        results.value = itemsList
                    }

                    override fun onCancelled(error: DatabaseError) = Unit
                })
        }

        return results
    }

}