package com.canteenapp.views.customer

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.canteenapp.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class CustomerHome : AppCompatActivity() {

    private lateinit var customerHomeViewModel: CustomerHomeViewModel
    private val bagItem by lazy {
        findViewById<TextView>(R.id.bag_item_count)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViewModel()
        initLayout()
        setData()
    }

    private fun initLayout() {
        setContentView(R.layout.activity_customer_home)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)

        navView.setupWithNavController(navController)

        findViewById<LinearLayout>(R.id.bag).setOnClickListener {
            Intent(this, BagActivity::class.java).run {
                startActivity(this)
            }
        }
    }

    private fun initViewModel() {
        customerHomeViewModel =
            ViewModelProvider(this).get(CustomerHomeViewModel::class.java)
    }

    private fun setData() {
        customerHomeViewModel.getBagCount().observe(this, {
            bagItem.text = it.toString()
        })
    }

    override fun onResume() {
        super.onResume()
        customerHomeViewModel.getBagCount().observe(this, {
            bagItem.text = it.toString()
        })
    }

}
