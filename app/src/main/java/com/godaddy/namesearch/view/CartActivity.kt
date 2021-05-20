package com.godaddy.namesearch.view

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.godaddy.namesearch.model.AuthManager
import com.godaddy.namesearch.model.PaymentsManager
import com.godaddy.namesearch.R
import com.godaddy.namesearch.model.ShoppingCart
import com.godaddy.namesearch.view.adapter.CartAdapter
import com.godaddy.namesearch.viewmodel.DomainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.text.NumberFormat


class CartActivity : AppCompatActivity() {
    private val viewModel: DomainViewModel by lazy {
        ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())
                .get(DomainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        val cartRecyclerView = findViewById<RecyclerView>(R.id.cart_item_recyclerview)
        cartRecyclerView.layoutManager = LinearLayoutManager(this)
        val dividerItemDecoration = DividerItemDecoration(
            cartRecyclerView.context,
            LinearLayout.VERTICAL
        )
        cartRecyclerView.addItemDecoration(dividerItemDecoration)
        cartRecyclerView.adapter = CartAdapter { updatePayButton() }

        findViewById<Button>(R.id.pay_now_button).setOnClickListener { payButtonTapped() }
    }

    override fun onResume() {
        super.onResume()
        updatePayButton()
    }

    private fun payButtonTapped() {
        if (PaymentsManager.selectedPaymentMethod == null) {
            startActivity(Intent(this, PaymentMethodActivity::class.java))
        } else {
            performPayment()
        }
    }

    private fun updatePayButton() {
        if (PaymentsManager.selectedPaymentMethod == null) {
            findViewById<Button>(R.id.pay_now_button).text = "Select a Payment Method"
        } else {
            var totalPayment = 0.00

            ShoppingCart.domains.forEach {
                val priceDouble = it.price.replace("$","").toDouble()
                totalPayment += priceDouble
            }

            val currencyFormatter = NumberFormat.getCurrencyInstance()

            findViewById<Button>(R.id.pay_now_button).text = "Pay ${currencyFormatter.format(totalPayment)} Now"
        }
    }

    private fun performPayment() {
        findViewById<Button>(R.id.pay_now_button).isEnabled = false
        lifecycleScope.launch {
            val result = viewModel.postPayment()
            AlertDialog.Builder(this@CartActivity)
                .setTitle("All done!")
                .setMessage(result)
                .show()
            findViewById<Button>(R.id.pay_now_button).isEnabled = true
        }
    }
}