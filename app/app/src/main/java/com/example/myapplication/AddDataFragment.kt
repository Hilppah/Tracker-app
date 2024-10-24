package com.example.myapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast


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
        val hoursSlept: Float

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
            val hoursSlept = hoursSlept.text.toString()

            if (hoursSlept.isEmpty() || selectedEmotionsList.isEmpty()) {
                Toast.makeText((requireContext()), "Please fill all the fields", Toast.LENGTH_SHORT).show()
            } else {
                val emotionsString = selectedEmotionsList.joinToString(separator = ", ")
                val painString = selectedEmotionsList.joinToString(separator = ", ")
                val dailyActivity = DailyActivity(selectedDate.toString(), emotionsString, painString, hoursSlept.toFloat(),)


                Toast.makeText((requireContext()), "Data saved successfully", Toast.LENGTH_SHORT).show()
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
}
