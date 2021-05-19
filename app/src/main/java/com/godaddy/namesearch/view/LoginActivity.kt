package com.godaddy.namesearch.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.lifecycleScope
import com.godaddy.namesearch.model.AuthManager
import com.godaddy.namesearch.R
import com.godaddy.namesearch.model.LoginResponse
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        findViewById<Button>(R.id.login).setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        val username = findViewById<EditText>(R.id.username).text.toString()
        val password = findViewById<EditText>(R.id.password).text.toString()


        lifecycleScope.launch {
            performLogin(username, password)
            startActivity(Intent(this@LoginActivity, DomainSearchActivity::class.java))
        }
    }

    private suspend fun performLogin(username: String, password: String) {
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
                while(responseLine != null) {
                    response.append(responseLine)
                    responseLine = it.readLine()
                }
                val loginResponse = Gson().fromJson(response.toString(), LoginResponse::class.java)
                AuthManager.user = loginResponse.user
                AuthManager.token = loginResponse.auth.token
            }
        }
    }
}