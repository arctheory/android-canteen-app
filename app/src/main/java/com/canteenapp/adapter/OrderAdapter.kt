package com.canteenapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.canteenapp.R
import com.canteenapp.data.OrderItem

class OrderAdapter(private val orderList: List<OrderItem>): RecyclerView.Adapter<OrderAdapter.OrderHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHolder {
        val itemHolder = LayoutInflater.from(parent.context)
                .inflate(R.layout.cart_item, parent, false)

        return OrderHolder(itemHolder)
    }

    override fun onBindViewHolder(holder: OrderHolder, position: Int) {
        val orderData = orderList[position]
        holder.bindOrder(orderData)
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    class OrderHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @SuppressLint("SetTextI18n")
        fun bindOrder(order: OrderItem) {
            itemView.findViewById<TextView>(R.id.order_id).apply {
                text = order.orderId
            }
            itemView.findViewById<TextView>(R.id.order_price).apply {
                text = "${order.price}/-"
            }
            if ( order.isDelivered ) {
                itemView.findViewById<TextView>(R.id.order_state).apply {
                    text = "Order delivered"
                }
            }
        }

        companion object {
            private val ORDER_ITEM = "ORDER_ITEM_NOT_DELIVERED"
        }
    }
}