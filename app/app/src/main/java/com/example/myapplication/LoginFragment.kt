package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.util.Log
import android.widget.Toast
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

class LoginFragment : Fragment() {

    private lateinit var emailText: EditText
    private lateinit var passwordText: EditText
    private lateinit var loginButton: Button
    private lateinit var switchViewToRegister: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        emailText = view.findViewById(R.id.editTextTextEmailAddress)
        passwordText = view.findViewById(R.id.editTextTextPassword)
        loginButton = view.findViewById(R.id.buttonLogin)
        switchViewToRegister = view.findViewById(R.id.buttonSwitchToRegister)


        loginButton.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch{
                val isLoginSuccessful = login()
                Log.d("is login sucessful", isLoginSuccessful.toString())
                if (isLoginSuccessful == true) {
                    Log.d("LoginFragment", "Login successful!")
                    Toast.makeText(requireContext(), "Login successful!", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, CalendarFragment())
                        .addToBackStack(null)
                        .commit()
                } else {
                    Log.d("LoginFragment", "Login failed")
                    Toast.makeText(requireContext(), "Login failed", Toast.LENGTH_SHORT).show()
                }
            }
        }

        switchViewToRegister.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, RegisterFragment())
                .addToBackStack(null)
                .commit()
        }

        return view
    }

    private suspend fun login(): Boolean {
        val loginData =
            mapOf("email" to emailText.text.toString(), "password" to passwordText.text.toString())
        val mapper = jacksonObjectMapper()
        val jsonString = mapper.writeValueAsString(loginData)
        val requestBody = jsonString.toRequestBody("application/json; charset=utf-8".toMediaType())

        return withContext(Dispatchers.IO) {
            try {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("http://10.0.2.2:5000/login")
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()

                if (response.code == 200) {
                    Log.d("httpresponse", "Login successful: ${response.message}")
                    return@withContext true
                } else {
                    Log.d("httpresponse", "Login failed: ${response.message}")
                    return@withContext false
                }
            } catch (e: Exception) {
                Log.e("httpresponse", "Request failed: ${e.message}")
                return@withContext false
            }
        }
    }
}


