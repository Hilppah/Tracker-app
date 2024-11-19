package com.example.myapplication

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

class ProfileFragment : Fragment() {

    private lateinit var deleteUserButton: Button
    private lateinit var deleteAllButton: Button
    private lateinit var calendarButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        deleteAllButton = view.findViewById(R.id.buttonData)
        deleteUserButton = view.findViewById(R.id.buttonUser)
        calendarButton = view.findViewById(R.id.buttonCalendar)

        calendarButton.setOnClickListener{
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, CalendarFragment())
                .addToBackStack(null)
                .commit()
        }

        deleteAllButton.setOnClickListener{
            CoroutineScope(Dispatchers.Main).launch {
                val isAllDeleted = deleteAll()
                if (isAllDeleted){
                    Toast.makeText(requireContext(), "Information of all of the days deleted successfully", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, CalendarFragment())
                        .addToBackStack(null)
                        .commit()
                } else {
                    Toast.makeText(requireContext(), "Deletion failed", Toast.LENGTH_SHORT).show()
                }
            }
        }

        deleteUserButton.setOnClickListener{
            CoroutineScope(Dispatchers.Main).launch {
                val isUserDeleted = deleteUser()
                if (isUserDeleted){
                    Toast.makeText(requireContext(), "Account deleted", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, RegisterFragment())
                        .addToBackStack(null)
                        .commit()
                } else {
                    Toast.makeText(requireContext(), "Deletion failed", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return view
    }

    private suspend fun deleteAll(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val client = OkHttpClient()
                val sharedPreferences =
                    requireContext().getSharedPreferences("user_prefs", MODE_PRIVATE)
                val userId = sharedPreferences.getInt("user_id", 0)
                val url = "http://10.0.2.2:5000/delete?&user_id=$userId"

                val request = Request.Builder()
                    .url(url)
                    .delete()
                    .build()

                val response =client.newCall(request).execute()
                if (response.isSuccessful){
                    return@withContext true
                }else{
                    Log.e("DeleteDay", "Delete all error")
                    return@withContext false
                }
            } catch (e: Exception) {
                Log.e("DeleteDay", "Delete all exception")
                return@withContext false
            }
        }
    }

    private suspend fun deleteUser(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val client = OkHttpClient()
                val sharedPreferences =
                    requireContext().getSharedPreferences("user_prefs", MODE_PRIVATE)
                val userId = sharedPreferences.getInt("user_id", 0)
                val url = "http://10.0.2.2:5000/deleteUser?&user_id=$userId"

                val request = Request.Builder()
                    .url(url)
                    .delete()
                    .build()

                val response =client.newCall(request).execute()
                if (response.isSuccessful){
                    return@withContext true
                }else{
                    Log.e("DeleteDay", "Delete user error")
                    return@withContext false
                }
            } catch (e: Exception) {
                Log.e("DeleteDay", "Delete user exception")
                return@withContext false
            }
        }
    }
}
