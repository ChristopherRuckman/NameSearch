package com.godaddy.namesearch.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.godaddy.namesearch.R
import com.godaddy.namesearch.model.PaymentMethod

class PaymentMethodAdapter(
    context: Context
): ArrayAdapter<PaymentMethod>(context, -1,  mutableListOf()) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var itemView = convertView
        if (itemView == null) {
            itemView = LayoutInflater.from(context)
                .inflate(R.layout.item_domain_result, parent, false)
        }

        itemView?.apply {
            val item = getItem(position)
            findViewById<TextView>(R.id.name_text_view).text = item?.name
            findViewById<TextView>(R.id.price_text_view).text =  item?.lastFour?.also { "Ending in $it" } ?: item?.displayFormattedEmail
            tag = item
        }

        return itemView!!
    }
}
