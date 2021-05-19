package com.godaddy.namesearch

import android.content.Context
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class PaymentMethodActivity : AppCompatActivity() {
    var paymentMethods: List<PaymentMethod> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_method)
        val paymentMethodList = findViewById<ListView>(R.id.payment_method_list)
        val paymentMethodAdapter = PaymentMethodAdapter(this)
        paymentMethodList.adapter = paymentMethodAdapter
        paymentMethodList.setOnItemClickListener { _, _, position, _ ->
            PaymentsManager.selectedPaymentMethod = paymentMethods[position]
            finish()
        }


        lifecycleScope.launch {
            paymentMethods = fetchPaymentMethods()
            paymentMethodAdapter.addAll(paymentMethods)
        }
    }

    private suspend fun fetchPaymentMethods(): List<PaymentMethod> {
        return withContext(Dispatchers.IO) {
            val url = URL("https://gd.proxied.io/user/payment-methods")
            val connection = url.openConnection()
            connection.connect()
            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            val response = StringBuilder()
            var responseLine = reader.readLine()
            while (responseLine != null) {
                response.append(responseLine)
                responseLine = reader.readLine()
            }
            val paymentListType = object : TypeToken<List<PaymentMethod>>() {}.type
            Gson().fromJson(response.toString(), paymentListType)
        }
    }

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
}
