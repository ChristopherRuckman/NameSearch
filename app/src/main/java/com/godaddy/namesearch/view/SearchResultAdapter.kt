package com.godaddy.namesearch.view

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.godaddy.namesearch.R
import com.godaddy.namesearch.model.Domain

class SearchResultAdapter(
    context: Context
): ArrayAdapter<Domain>(context, -1,  mutableListOf()) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var itemView = convertView
        if (itemView == null) {
            itemView = LayoutInflater.from(context)
                .inflate(R.layout.item_domain_result, parent, false)
        }

        itemView?.apply {
            val item = getItem(position)
            findViewById<TextView>(R.id.name_text_view).text = item?.name
            findViewById<TextView>(R.id.price_text_view).text = item?.price
            itemView.setBackgroundColor(when (item!!.selected) {
                true -> Color.LTGRAY
                else -> Color.TRANSPARENT
            })
            tag = item
        }

        return itemView!!
    }
}
