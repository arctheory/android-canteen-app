package com.storeapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.storeapp.R
import com.storeapp.data.BagItem

class BagAdapter(private val bagList: List<BagItem>,private val listener: (BagItem) -> Unit): RecyclerView.Adapter<BagAdapter.BagItemHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BagItemHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.bag_item, parent, false)

        return BagItemHolder(itemView)
    }

    override fun onBindViewHolder(holder: BagItemHolder, position: Int) {
        val item = bagList[position]
        holder.bindItem(item)
        holder.itemView.findViewById<Button>(R.id.remove_btn).setOnClickListener { listener(item) }
    }

    override fun getItemCount(): Int {
        return bagList.size
    }

    class BagItemHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        @SuppressLint("SetTextI18n")
        fun bindItem(bagItem: BagItem) {
            itemView.findViewById<TextView>(R.id.item_name).apply {
                text = bagItem.product.name
            }
            itemView.findViewById<TextView>(R.id.item_price).apply {
                text = "${bagItem.product.price}/-"
            }
        }

        companion object {
            private val BAG_ITEM_TAG = "BAG_ITEM"
        }
    }
}