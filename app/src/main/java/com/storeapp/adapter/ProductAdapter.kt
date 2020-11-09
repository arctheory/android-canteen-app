package com.storeapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.storeapp.R
import com.storeapp.data.Product

class ProductAdapter(private val productList: List<Product>, private val listener: (Product) -> Unit): RecyclerView.Adapter<ProductAdapter.ProductHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_item, parent, false)

        return ProductHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProductHolder, position: Int) {
        val productData = productList[position]
        holder.bindProduct(productData)
        holder.itemView.findViewById<Button>(R.id.add_btn).setOnClickListener { listener(productData) }
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    class ProductHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @SuppressLint("SetTextI18n")
        fun bindProduct(product: Product) {
            itemView.findViewById<ImageView>(R.id.product_img).apply {
                Glide.with(itemView)
                    .load(product.imgUrl)
                    .fitCenter()
                    .into(this)
            }
            itemView.findViewById<TextView>(R.id.product_name).apply { text = product.name }
            itemView.findViewById<TextView>(R.id.product_price).apply { text = "${product.price}/-" }
        }

        companion object {
            private val PRODUCT_KEY = "PRODUCT"
        }
    }
}