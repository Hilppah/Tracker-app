package com.example.myapplication

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody


class AddDataFragment : Fragment() {

    private lateinit var buttonHappy: ImageButton
    private lateinit var buttonSad: ImageButton
    private lateinit var buttonAnxious: ImageButton
    private lateinit var buttonConfident: ImageButton
    private lateinit var buttonAngry: ImageButton
    private lateinit var buttonNoPain: ImageButton
    private lateinit var buttonFlu: ImageButton
    private lateinit var buttonFever: ImageButton
    private lateinit var buttonHeadache: ImageButton
    private lateinit var buttonCramps: ImageButton
    private lateinit var hoursSlept: EditText
    private lateinit var selectedDate: EditText
    private lateinit var  buttonSave: Button

    private var selectedEmotionsList = mutableListOf<String>()
    private var selectedPainList = mutableListOf<String>()

    data class DailyActivity(
        val date: String,
        val emotion: String,
        val pain: String,
        val hoursSlept: Float,
        val userId: Int

    )


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view= inflater.inflate(R.layout.fragment_add_data, container, false)

        buttonHappy = view.findViewById(R.id.imageButtonHappy)
        buttonSad = view.findViewById(R.id.imageButtonSad)
        buttonAnxious = view.findViewById(R.id.imageButtonAnxious)
        buttonConfident = view.findViewById(R.id.imageButtonConfident)
        buttonAngry = view.findViewById(R.id.imageButtonAngry)
        buttonNoPain = view.findViewById(R.id.imageButtonNo)
        buttonFlu = view.findViewById(R.id.imageButtonFlu)
        buttonFever = view.findViewById(R.id.imageButtonFever)
        buttonHeadache = view.findViewById(R.id.imageButtonHead)
        buttonCramps = view.findViewById(R.id.imageButtonCramps)
        hoursSlept = view.findViewById(R.id.editTextTime)
        selectedDate = view.findViewById(R.id.editTextDate)
        buttonSave = view.findViewById(R.id.buttonSave)



        buttonSave.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val hoursSlept = hoursSlept.text.toString()

                if (hoursSlept.isEmpty() || selectedEmotionsList.isEmpty() || selectedPainList.isEmpty()) {
                    Toast.makeText(
                        (requireContext()),
                        "Please fill all the fields",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val sharedPreferences = requireContext().getSharedPreferences("user_prefs", MODE_PRIVATE)
                    val userId = sharedPreferences.getInt("user_id", 0)

                    val emotionsString = selectedEmotionsList.joinToString(separator = ", ")
                    val painString = selectedPainList.joinToString(separator = ", ")
                    val dailyActivity = DailyActivity(
                        selectedDate.text.toString(),
                        emotionsString,
                        painString,
                        hoursSlept.toFloat(),
                        userId
                    )

                    val isDataSentSuccessful = sendData(dailyActivity)
                    if (isDataSentSuccessful == true) {
                        Toast.makeText((requireContext()), "Data saved successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText((requireContext()), "Data save failed", Toast.LENGTH_SHORT).show()
                    }


                }
            }
        }

        buttonHappy.setOnClickListener{
            toggleEmotion("happy", buttonHappy)
        }

        buttonSad.setOnClickListener{
            toggleEmotion("sad", buttonSad)
        }

        buttonConfident.setOnClickListener{
            toggleEmotion("confident", buttonConfident)
        }

        buttonAnxious.setOnClickListener{
            toggleEmotion("anxious", buttonAnxious)
        }

        buttonAngry.setOnClickListener{
            toggleEmotion("angry", buttonAngry)
        }

        buttonNoPain.setOnClickListener{
            togglePain("no pain", buttonNoPain)
        }

        buttonFlu.setOnClickListener{
            togglePain("Flu", buttonFlu)
        }

        buttonFever.setOnClickListener{
            togglePain("Fever", buttonFever)
        }

        buttonHeadache.setOnClickListener{
            togglePain("headache", buttonHeadache)
        }

        buttonCramps.setOnClickListener{
            togglePain("Cramps", buttonCramps)
        }

        return view

    }

    private fun toggleEmotion(emotion: String, button: ImageButton) {
        if (selectedEmotionsList.contains(emotion)) {
            selectedEmotionsList.remove(emotion)
            Toast.makeText(requireContext(), "$emotion removed", Toast.LENGTH_SHORT).show()
            button.alpha = 1.0f
        } else {
            selectedEmotionsList.add(emotion)
            Toast.makeText(requireContext(), "$emotion added", Toast.LENGTH_SHORT).show()

            button.alpha = 0.5f
        }
    }

    private fun togglePain(pain: String, button: ImageButton) {
        if (selectedPainList.contains(pain)) {
            selectedPainList.remove(pain)
            Toast.makeText(requireContext(), "$pain removed", Toast.LENGTH_SHORT).show()
            button.alpha = 1.0f
        } else {
            selectedPainList.add(pain)
            Toast.makeText(requireContext(), "$pain added", Toast.LENGTH_SHORT).show()

            button.alpha = 0.5f
        }
    }

    private suspend fun sendData(dailyActivity: DailyActivity): Boolean {
        val activityData = mapOf(
            "date" to dailyActivity.date,
            "emotion" to dailyActivity.emotion,
            "pain" to dailyActivity.pain,
            "hoursSlept" to dailyActivity.hoursSlept,
            "user_id" to dailyActivity.userId
        )
        val mapper = jacksonObjectMapper()
        val jsonString = mapper.writeValueAsString(activityData)
        val requestBody = jsonString.toRequestBody("application/json; charset=utf-8".toMediaType())
        Log.d("sendData", "sendData:  ${jsonString}")

        return withContext(Dispatchers.IO) {
            try {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("http://10.0.2.2:5000/addData")
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()

                if (response.code ==201) {
                    return@withContext true
                } else {
                    return@withContext false
                }

            } catch (e: Exception) {
                Log.e("httpresponse", "Request failed: ${e.message}")
                return@withContext false
            }
        }
    }

}

