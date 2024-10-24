package com.example.myapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView


class CalendarFragment : Fragment() {

    private lateinit var calendar: CalendarView
    private lateinit var buttonFeeling: Button
    private lateinit var buttonProfile: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calendar, container, false)

        calendar = view.findViewById(R.id.calendarView)
        buttonFeeling = view.findViewById(R.id.buttonFeeling)
        buttonProfile = view.findViewById(R.id.buttonProfile)

        buttonFeeling.setOnClickListener{
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, AddDataFragment())
                .addToBackStack(null)
                .commit()
        }

        buttonProfile.setOnClickListener{
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ProfileFragment())
                .addToBackStack(null)
                .commit()
        }

        calendar.setOnDateChangeListener{ view, year, month, dayOfMonth ->
            val date = "$dayOfMonth/${month + 1}/$year"
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, SelectedDateFragment.newInstance(date))
                .addToBackStack(null)
                .commit()

        }

        return view
    }




}
