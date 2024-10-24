package com.example.myapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

class SelectedDateFragment : Fragment() {

    private lateinit var displayDate: Button
    private lateinit var deleteButton: Button
    private lateinit var howFeelButton: Button
    private lateinit var feelingInfo: TextView

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


       return view
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