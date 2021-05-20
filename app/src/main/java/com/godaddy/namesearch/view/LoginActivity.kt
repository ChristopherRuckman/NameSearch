package com.godaddy.namesearch.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.godaddy.namesearch.R
import com.godaddy.namesearch.viewmodel.DomainViewModel
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private val viewModel: DomainViewModel by lazy {
        ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())
                .get(DomainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        findViewById<Button>(R.id.login).setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        val username = findViewById<EditText>(R.id.username).text.toString()
        val password = findViewById<EditText>(R.id.password).text.toString()


        lifecycleScope.launch {
            viewModel.performLogin(username, password)
            startActivity(Intent(this@LoginActivity, DomainSearchActivity::class.java))
        }
    }
}