package com.example.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            loadFragment(LoginFragment())
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.commit {
            replace(R.id.fragmentContainer, fragment)
            addToBackStack(null)
        }
    }

}

