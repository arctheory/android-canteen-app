package com.storeapp.views.customer

import android.content.Intent
import android.os.Bundle
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.storeapp.R

class CustomerHome : AppCompatActivity() {

    private lateinit var customerHomeViewModel: CustomerHomeViewModel
    private val bagItem by lazy {
        findViewById<TextView>(R.id.bag_count_text)
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

        findViewById<RelativeLayout>(R.id.bag).setOnClickListener {
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
