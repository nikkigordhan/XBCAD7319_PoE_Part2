package com.example.xbcad7319_physiotherapyapp.ui.intake_forms

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.xbcad7319_physiotherapyapp.R

class IntakeFormsFragment : Fragment() {

    companion object {
        fun newInstance() = IntakeFormsFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_intake_forms, container, false)

        // add code logic here.
    }
}