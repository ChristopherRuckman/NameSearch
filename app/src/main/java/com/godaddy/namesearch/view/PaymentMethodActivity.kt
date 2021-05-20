package com.godaddy.namesearch.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.godaddy.namesearch.model.PaymentsManager
import com.godaddy.namesearch.R
import com.godaddy.namesearch.model.PaymentMethod
import com.godaddy.namesearch.view.adapter.PaymentMethodAdapter
import com.godaddy.namesearch.viewmodel.DomainViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL

class PaymentMethodActivity : AppCompatActivity() {
    private val viewModel: DomainViewModel by lazy {
        ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())
                .get(DomainViewModel::class.java)
    }

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
            paymentMethods = viewModel.fetchPaymentMethods()
            paymentMethodAdapter.addAll(paymentMethods)
        }
    }
}
