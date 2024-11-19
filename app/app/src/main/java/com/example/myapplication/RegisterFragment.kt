package com.example.myapplication

import android.annotation.SuppressLint
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
            CoroutineScope(Dispatchers.Main).launch {
                val registerSuccessful = register()
                if (registerSuccessful) {
                    Log.d("register fragment", "successful")
                    Toast.makeText(requireContext(), "Register successful", Toast.LENGTH_SHORT)
                        .show()
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, LoginFragment())
                        .addToBackStack(null)
                        .commit()

                } else {
                    Log.d("RegisterFragment", "Registration failed")
                    Toast.makeText(requireContext(), "Registration failed", Toast.LENGTH_SHORT)
                        .show()
                }
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

    @SuppressLint("SuspiciousIndentation")
    private suspend fun register(): Boolean {
        val registerData = mapOf(
            "email" to emailText.text.toString(),
            "password" to passwordText.text.toString()
        )
        val mapper = jacksonObjectMapper()
        val jsonString = mapper.writeValueAsString(registerData)
        val requestBody =
            jsonString.toRequestBody("application/json; charset=utf-8".toMediaType())

        return withContext(Dispatchers.IO) {
            try {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("http://10.0.2.2:5000/register")
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()
                    if (response.code == 201) {
                        Log.d("httpresponse", "Register successful: ${response.message}")
                        return@withContext true
                    } else {
                        Log.d("httpresponse", "Register failed: ${response.message}")
                        return@withContext false
                    }

            } catch (e: Exception) {
                    Log.e("httpresponse", "Request failed: ${e.message}")
                return@withContext false
            }
        }
    }
}
