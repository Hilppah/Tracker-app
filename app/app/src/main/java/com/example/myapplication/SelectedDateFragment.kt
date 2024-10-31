package com.example.myapplication

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
    private lateinit var howFeelButton: Button
    private lateinit var feelingInfo: TextView

    data class DailyActivity @JsonCreator constructor(
        @JsonProperty("date") val date: String,
        @JsonProperty("emotion") val emotion: String,
        @JsonProperty("pain") val pain: String,
        @JsonProperty("hoursSlept") val hoursSlept: Float

    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view= inflater.inflate(R.layout.fragment_selected_date, container, false)

        displayDate = view.findViewById(R.id.editTextSelectedDate)
        deleteButton =view.findViewById(R.id.buttonDelete)
        howFeelButton = view.findViewById(R.id.buttonAdd)
        feelingInfo = view.findViewById(R.id.textViewInfo)


        val selectedDate = arguments?.getString("selected_date")
        Log.d("selected date", "selected date $selectedDate" )

        if (selectedDate != null) {
             CoroutineScope(Dispatchers.Main).launch {
                 val data = getData(selectedDate)
                 if (data == null) {
                     //returns to calendar
                     println(data)

                 } else {
                     println(data)
                     activity?.runOnUiThread {
                         updateViews(data)
                     }
                 }
            }
        }

       return view
    }

    fun updateViews(data: DailyActivity) {
        feelingInfo.text = data.emotion + data.pain + data.hoursSlept
        Log.d("updateview", "updateview ${data.emotion}" )
    }

    private suspend fun getData(selectedDate: String): DailyActivity? {
        return withContext(Dispatchers.IO) {
            try {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("http://10.0.2.2:5000/getData")
                    .get()
                    .build()

                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val objectMapper = ObjectMapper()
                    val responseData = response.body?.string() ?: ""
                    objectMapper.readValue(responseData, DailyActivity::class.java)
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