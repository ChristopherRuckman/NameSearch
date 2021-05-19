package com.godaddy.namesearch.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.godaddy.namesearch.R
import com.godaddy.namesearch.model.ShoppingCart
import com.godaddy.namesearch.model.Domain

class CartAdapter(private val onItemRemoved: () -> Unit): RecyclerView.Adapter<CartAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: Domain, onItemRemoved: () -> Unit) {
            itemView.findViewById<TextView>(R.id.name_text_view).text = item.name
            itemView.findViewById<TextView>(R.id.price_text_view).text = item.price
            itemView.findViewById<ImageButton>(R.id.remove_button).setOnClickListener {
                ShoppingCart.domains = ShoppingCart.domains.filter { it.name != item.name }
                onItemRemoved()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(ShoppingCart.domains[position]) {
            onItemRemoved()
            notifyDataSetChanged()
        }
    }

    override fun getItemCount() = ShoppingCart.domains.size
}