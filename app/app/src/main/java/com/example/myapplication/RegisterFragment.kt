package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.util.Log
import androidx.fragment.app.Fragment
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class RegisterFragment : Fragment() {

    private lateinit var emailText: EditText
    private lateinit var passwordText: EditText
    private lateinit var registerButton: Button
    private lateinit var switchViewToLogin: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_register, container, false)


        emailText = view.findViewById(R.id.editTextTextEmailAddress)
        passwordText = view.findViewById(R.id.editTextTextPassword)
        registerButton = view.findViewById(R.id.buttonRegister)
        switchViewToLogin = view.findViewById(R.id.buttonSwitchToLogin)


        registerButton.setOnClickListener {
            val registerSuccessful = register()
            if (registerSuccessful) {
                Log.d("register fragment", "successful")
            } else {
                Log.d("RegisterFragment", "Registration failed")
            }
        }

        switchViewToLogin.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, LoginFragment())
                .addToBackStack(null)
                .commit()
        }

        return view
    }

    private fun register(): Boolean {
        val registerData = mapOf(
            "email" to emailText.text.toString(),
            "password" to passwordText.text.toString()
        )
        val mapper = jacksonObjectMapper()
        val jsonString = mapper.writeValueAsString(registerData)
        val requestBody =
            jsonString.toRequestBody("application/json; charset=utf-8".toMediaType())

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("http://10.0.2.2:5000/register")
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()

                withContext(Dispatchers.Main) {
                    if (response.code == 201) {
                        Log.d("httpresponse", "Register successful: ${response.message}")
                        return@withContext true
                    } else {
                        Log.d("httpresponse", "Register failed: ${response.message}")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("httpresponse", "Request failed: ${e.message}")
                }
            }
        }
        return false
    }
}
