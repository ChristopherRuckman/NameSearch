package com.godaddy.namesearch.viewmodel

import androidx.lifecycle.ViewModel
import com.godaddy.namesearch.model.AuthManager
import com.godaddy.namesearch.model.LoginResponse
import com.google.gson.Gson
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