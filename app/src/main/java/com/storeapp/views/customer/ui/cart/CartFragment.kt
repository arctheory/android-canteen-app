package com.storeapp.views.customer.ui.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.storeapp.R
import com.storeapp.adapter.OrderAdapter

class CartFragment : Fragment() {

    private lateinit var cartViewModel: CartViewModel
    private lateinit var root: View
    private lateinit var inProgressOrderList: RecyclerView
    private lateinit var inProgressOrderAdapter: RecyclerView.Adapter<*>
    private lateinit var inProgressOrderLayoutManager: LinearLayoutManager
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

        inProgressOrderLayoutManager = LinearLayoutManager(root.context, LinearLayoutManager.VERTICAL, false)
        successfulOrderLayoutManager = LinearLayoutManager(root.context, LinearLayoutManager.VERTICAL, false)

        inProgressOrderList = root.findViewById(R.id.in_progress)
        successfulOrderList = root.findViewById(R.id.delivered)

        cartViewModel.getItems().observe(viewLifecycleOwner, {
            inProgressOrderAdapter = OrderAdapter(it.filter { order -> !order.isDelivered })

            inProgressOrderList.apply {
                setHasFixedSize(true)
                layoutManager = inProgressOrderLayoutManager
                adapter = inProgressOrderAdapter
            }

            successfulOrderAdapter = OrderAdapter(it.filter { order -> order.isDelivered })

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
            inProgressOrderList.adapter = OrderAdapter(it.filter { order -> !order.isDelivered })
            successfulOrderList.adapter = OrderAdapter(it.filter { order -> order.isDelivered })
        })
    }
}