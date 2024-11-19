package com.example.myapplication

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

class SelectedDateFragment : Fragment() {

    private lateinit var displayDate: TextView
    private lateinit var deleteButton: Button
    private lateinit var feelingInfo: TextView
    private lateinit var profileButton: Button

    data class DailyActivity @JsonCreator constructor(
        @JsonProperty("activity_date") val date: String,
        @JsonProperty("emotion") val emotion: String,
        @JsonProperty("pain") val pain: String,
        @JsonProperty("hours_slept") val hoursSlept: Float

    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        val view= inflater.inflate(R.layout.fragment_selected_date, container, false)

        val selectedDate = arguments?.getString("selected_date")
        Log.d("selected date", "selected date $selectedDate" )

        displayDate = view.findViewById(R.id.editTextSelectedDate)
        deleteButton =view.findViewById(R.id.buttonDelete)
        feelingInfo = view.findViewById(R.id.textViewInfo)
        profileButton = view.findViewById(R.id.buttonProfile)

        displayDate.text = selectedDate
        if (selectedDate != null) {
             CoroutineScope(Dispatchers.Main).launch {
                 val data = getData(selectedDate)
                 if (data == null) {
                     parentFragmentManager.beginTransaction()
                         .replace(R.id.fragmentContainer, CalendarFragment())
                         .addToBackStack(null)
                         .commit()
                     println(data)

                 } else {
                     println(data)
                     activity?.runOnUiThread {
                         updateViews(data)
                     }
                 }
            }

            profileButton.setOnClickListener{
                parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ProfileFragment())
                .addToBackStack(null)
                .commit()}

            deleteButton.setOnClickListener{
                CoroutineScope(Dispatchers.Main).launch {
                    val isDayDeleted = deleteDay(selectedDate)
                    if (isDayDeleted){
                        Toast.makeText(requireContext(), "Information of the day deleted successfully", Toast.LENGTH_SHORT).show()
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainer, CalendarFragment())
                            .addToBackStack(null)
                            .commit()
                    } else {
                        Toast.makeText(requireContext(), "Deletion failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

       return view
    }



    fun updateViews(data: DailyActivity) {
        val emotions = data.emotion.split(", ").joinToString(", ")
        val pains = data.pain.split(", ").joinToString(", ")
        val hoursSlept = data.hoursSlept.toString()

        feelingInfo.text = "Emotion: $emotions\nPain: $pains\nHours Slept: $hoursSlept"
        Log.d("updateview", "updateview Emotion: $emotions, Pain: $pains, Hours Slept: $hoursSlept")
    }

    private suspend fun getData(selectedDate: String): DailyActivity? {
        return withContext(Dispatchers.IO) {
            try {
                val client = OkHttpClient()
                val sharedPreferences = requireContext().getSharedPreferences("user_prefs", MODE_PRIVATE)
                val userId = sharedPreferences.getInt("user_id", 0)
                val url ="http://10.0.2.2:5000/activity?&date=$selectedDate&user_id=$userId"


                val request = Request.Builder()
                    .url(url)
                    .get()
                    .build()

                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val objectMapper = ObjectMapper()
                    val responseData = response.body?.string() ?: ""
                    val dailyActivity = objectMapper.readValue(responseData, DailyActivity::class.java)

                    val emotionsList = dailyActivity.emotion.split(",").map { it.trim() }
                    val painsList = dailyActivity.pain.split(",").map { it.trim() }
                    dailyActivity.copy(emotion = emotionsList.joinToString(", "), pain = painsList.joinToString(", "))
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Failed to fetch activity with selected date $selectedDate", Toast.LENGTH_SHORT).show()
                    }
                    null
                }
            } catch (e: Exception) {
                Log.e("httpresponseSelectedDate", "Request failed: ${e.message}")
                null
            }
        }

    }

    private suspend fun deleteDay(date : String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val client = OkHttpClient()
                val sharedPreferences =
                    requireContext().getSharedPreferences("user_prefs", MODE_PRIVATE)
                val userId = sharedPreferences.getInt("user_id", 0)
                val url = "http://10.0.2.2:5000/delete?&user_id=$userId&date=$date"

                val request = Request.Builder()
                    .url(url)
                    .delete()
                    .build()

                val response =client.newCall(request).execute()
                if (response.isSuccessful){
                    return@withContext true
                }else{
                    Log.e("DeleteDay", "Delete day error")
                    return@withContext false
                }
            } catch (e: Exception) {
                Log.e("DeleteDay", "Delete day exception")
                return@withContext false
            }
        }
    }


    companion object {
        fun newInstance(date: String): SelectedDateFragment {
            val fragment = SelectedDateFragment()
            val args = Bundle()
            args.putString("selected_date", date)
            fragment.arguments = args
            return fragment
        }
    }

}