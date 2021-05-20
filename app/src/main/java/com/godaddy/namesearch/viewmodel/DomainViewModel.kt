package com.godaddy.namesearch.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.godaddy.namesearch.DomainSearchExactMatchResponse
import com.godaddy.namesearch.DomainSearchRecommendedResponse
import com.godaddy.namesearch.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL

class DomainViewModel() : ViewModel() {
    suspend fun performLogin(username: String, password: String) {
        withContext(Dispatchers.IO) {
            val url = URL("https://gd.proxied.io/auth/login")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json; utf-8")
            connection.setRequestProperty("Accept", "application/json");
            connection.doOutput = true
            val loginRequestJson = """{
                "username":"$username",
                "password":"$password"
            }"""
            connection.outputStream.also {
                val input = loginRequestJson.toByteArray()
                it.write(input, 0, input.size)
            }
            connection.connect()
            BufferedReader(InputStreamReader(connection.inputStream)).also {
                val response = StringBuilder()
                var responseLine = it.readLine()
                while (responseLine != null) {
                    response.append(responseLine)
                    responseLine = it.readLine()
                }
                val loginResponse = Gson().fromJson(response.toString(), LoginResponse::class.java)
                AuthManager.user = loginResponse.user
                AuthManager.token = loginResponse.auth.token
            }
        }
    }

    suspend fun performSearch(query: String): List<Domain> {
        val results: List<Domain>
        return withContext(Dispatchers.IO) {
            val uri = Uri.parse("https://gd.proxied.io/search/exact")
                    .buildUpon()
                    .appendQueryParameter("q", query)
            val url = URL(uri.toString())
            val connection = url.openConnection() as HttpURLConnection
            connection.setRequestProperty("Content-Type", "application/json; utf-8")
            connection.setRequestProperty("Accept", "application/json");
            connection.connect()
            BufferedReader(InputStreamReader(connection.inputStream)).also {
                val response = StringBuilder()
                var responseLine = it.readLine()
                while (responseLine != null) {
                    response.append(responseLine)
                    responseLine = it.readLine()
                }

                val exactMatchResponse =
                        Gson().fromJson(response.toString(), DomainSearchExactMatchResponse::class.java)

                val uri = Uri.parse("https://gd.proxied.io/search/spins")
                        .buildUpon()
                        .appendQueryParameter("q", query)
                val url = URL(uri.toString())
                val connection = url.openConnection() as HttpURLConnection
                connection.setRequestProperty("Content-Type", "application/json; utf-8")
                connection.setRequestProperty("Accept", "application/json");
                connection.connect()
                BufferedReader(InputStreamReader(connection.inputStream)).also {
                    val response = StringBuilder()
                    var responseLine = it.readLine()
                    while (responseLine != null) {
                        response.append(responseLine)
                        responseLine = it.readLine()
                    }
                    val suggestionsResponse = Gson().fromJson(
                            response.toString(),
                            DomainSearchRecommendedResponse::class.java
                    )

                    val exactDomainPriceInfo = exactMatchResponse.products.first { product ->
                        product.productId == exactMatchResponse.domain.productId
                    }.priceInfo
                    val exactDomain = Domain(
                            exactMatchResponse.domain.fqdn,
                            exactDomainPriceInfo.currentPriceDisplay,
                            exactMatchResponse.domain.productId
                    )

                    val suggestionDomains = suggestionsResponse.domains.map { domain ->
                        val priceInfo = suggestionsResponse.products
                                .first { price -> price.productId == domain.productId }
                                .priceInfo

                        Domain(domain.fqdn, priceInfo.currentPriceDisplay, domain.productId)
                    }

                    results = listOf(exactDomain) + suggestionDomains
                }
            }
            results
        }
    }

    suspend fun postPayment(): String {
        return withContext(Dispatchers.IO) {
            val url = URL("https://gd.proxied.io/payments/process")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json; utf-8")
            connection.setRequestProperty("Accept", "application/json");
            connection.doOutput = true
            val paymentRequestJson = """{
                "auth":"${AuthManager.token}",
                "token":"${PaymentsManager.selectedPaymentMethod?.token}"
            }"""
            connection.outputStream.also {
                val input = paymentRequestJson.toByteArray()
                it.write(input, 0, input.size)
            }
            connection.connect()

            if(connection.responseCode == HttpURLConnection.HTTP_OK) {
                "Your purchase is complete!"
            } else {
                "There was an issue with your purchase"
            }
        }
    }

    suspend fun fetchPaymentMethods(): List<PaymentMethod> {
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
}