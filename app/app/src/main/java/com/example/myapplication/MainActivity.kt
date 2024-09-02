package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    GreetingWithNetworkRequest(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun GreetingWithNetworkRequest(modifier: Modifier = Modifier) {
    // Create a state variable to hold the greeting message
    var greeting by remember { mutableStateOf("Loading...") }

    // Create a coroutine scope
    val coroutineScope = rememberCoroutineScope()

    // Perform the HTTP request inside a coroutine
    LaunchedEffect(Unit) {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                val client = OkHttpClient()
                val request = Request.Builder().url("http://10.0.2.2:5000").build()
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                // Update the greeting message with the response
                if (responseBody != null) {
                    greeting = "Response: ${responseBody}"
                } else {
                    greeting = "Failed to load response"
                }
            } catch (e: IOException) {
                greeting = "Request failed: ${e.message}"
            }
        }
    }

    // Display the greeting message
    Text(
        text = greeting,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {
        GreetingWithNetworkRequest()
    }
}