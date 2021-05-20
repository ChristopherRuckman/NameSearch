package com.godaddy.namesearch.view

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.godaddy.namesearch.DomainSearchExactMatchResponse
import com.godaddy.namesearch.DomainSearchRecommendedResponse
import com.godaddy.namesearch.R
import com.godaddy.namesearch.model.ShoppingCart
import com.godaddy.namesearch.model.Domain
import com.godaddy.namesearch.view.adapter.SearchResultAdapter
import com.godaddy.namesearch.viewmodel.DomainViewModel
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class DomainSearchActivity : AppCompatActivity() {
    private val viewModel: DomainViewModel by lazy {
        ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())
                .get(DomainViewModel::class.java)
    }

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
            val results = viewModel.performSearch(searchQuery)
            searchResultAdapter.clear()
            searchResultAdapter.addAll(results)
        }
    }
}