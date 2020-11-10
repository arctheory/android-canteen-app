package com.canteenapp.views.customer.ui.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.canteenapp.R
import com.canteenapp.adapter.OrderAdapter

class CartFragment : Fragment() {

    private lateinit var cartViewModel: CartViewModel
    private lateinit var root: View
    private lateinit var successfulOrderList: RecyclerView
    private lateinit var successfulOrderAdapter: RecyclerView.Adapter<*>
    private lateinit var successfulOrderLayoutManager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        cartViewModel =
            ViewModelProvider(this).get(CartViewModel::class.java)
        root = inflater.inflate(R.layout.fragment_cart, container, false)

        successfulOrderLayoutManager = LinearLayoutManager(root.context, LinearLayoutManager.VERTICAL, false)
        successfulOrderList = root.findViewById(R.id.delivered)

        cartViewModel.getItems().observe(viewLifecycleOwner, {

            successfulOrderAdapter = OrderAdapter(it)

            successfulOrderList.apply {
                setHasFixedSize(true)
                layoutManager = successfulOrderLayoutManager
                adapter = successfulOrderAdapter
            }
        })

        return root
    }

    override fun onResume() {
        super.onResume()
        cartViewModel.getItems().observe(viewLifecycleOwner, {
            successfulOrderList.adapter = OrderAdapter(it)
        })
    }
}