package com.godaddy.namesearch.view

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.godaddy.namesearch.DomainSearchExactMatchResponse
import com.godaddy.namesearch.DomainSearchRecommendedResponse
import com.godaddy.namesearch.R
import com.godaddy.namesearch.model.ShoppingCart
import com.godaddy.namesearch.model.Domain
import com.godaddy.namesearch.view.adapter.SearchResultAdapter
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class DomainSearchActivity : AppCompatActivity() {
    lateinit var searchResultAdapter: SearchResultAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_domain_search)
        searchResultAdapter = SearchResultAdapter(this)

        findViewById<Button>(R.id.search_button).setOnClickListener {
            loadData()
            (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).apply {
                hideSoftInputFromWindow(currentFocus?.windowToken, 0)
            }
        }
        configureCartButton()
        findViewById<Button>(R.id.view_cart_button).setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        findViewById<ListView>(R.id.results_list_view).also { listView ->
            listView.choiceMode = ListView.CHOICE_MODE_MULTIPLE
            listView.adapter = searchResultAdapter
            listView.setOnItemClickListener { _, view, position, _ ->
                val item = searchResultAdapter.getItem(position)
                ShoppingCart.domains = ShoppingCart.domains.toMutableList().also {
                    if (ShoppingCart.domains.contains(item)) {
                        it.remove(item)
                    } else {
                        item?.apply { it.add(item) }
                    }
                }
                item?.apply {
                    item.selected = !item.selected
                    view.setBackgroundColor(when (item.selected) {
                        true -> Color.LTGRAY
                        false -> Color.TRANSPARENT
                    })
                }
                configureCartButton()
            }
        }
    }

    private fun configureCartButton() {
        findViewById<Button>(R.id.view_cart_button).isEnabled = ShoppingCart.domains.isNotEmpty()
    }

    private fun loadData() {
        val searchQuery = findViewById<EditText>(R.id.search_edit_text).text.toString()
        lifecycleScope.launch {
            val results = performSearch(searchQuery)
            searchResultAdapter.clear()
            searchResultAdapter.addAll(results)
        }
    }

    private suspend fun performSearch(query: String): List<Domain> {
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
}