package com.storeapp.views.customer

import android.content.DialogInterface
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.storeapp.R
import com.storeapp.adapter.BagAdapter

class BagActivity : AppCompatActivity() {

    private lateinit var bagViewModel: BagViewModel
    private lateinit var bagList: RecyclerView
    private lateinit var bagAdapter: RecyclerView.Adapter<*>
    private lateinit var bagLayoutManager: RecyclerView.LayoutManager
    private lateinit var placeOrderButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bag)

        bagList = findViewById(R.id.bag_list)
        bagLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        bagViewModel = ViewModelProvider(this).get(BagViewModel::class.java)

        bagViewModel.getBagItems().observe(this, { bagItemList ->
            bagAdapter = BagAdapter(bagItemList) { item ->
                bagViewModel.removeItem(item.timestamp)
            }

            bagList.apply {
                setHasFixedSize(true)
                layoutManager = bagLayoutManager
                adapter = bagAdapter
            }
        })


        findViewById<Button>(R.id.place_order).setOnClickListener {
            bagViewModel.placeOrder() {
                if ( it ) {
                    AlertDialog.Builder(this).apply {
                        setTitle("Order Status")
                        setMessage("Order placed successfully!")
                        setPositiveButton("Done", (DialogInterface.OnClickListener { _, _ -> finish() }))
                    }.run {
                        this.create().run { show() }
                    }
                } else {
                    AlertDialog.Builder(this).apply {
                        setTitle("Order Status")
                        setMessage("Failed to place order")
                        setPositiveButton("Try Again", null)
                    }.run { this.create().run { show() } }
                }
            }

        }

        findViewById<ImageButton>(R.id.back_btn).setOnClickListener { finish() }
    }

    override fun onResume() {
        super.onResume()
        bagViewModel.getBagItems().observe(this, { bagItemList ->
            bagList.adapter = BagAdapter(bagItemList) { item ->
                bagViewModel.removeItem(item.timestamp)
            }
        })
    }
}