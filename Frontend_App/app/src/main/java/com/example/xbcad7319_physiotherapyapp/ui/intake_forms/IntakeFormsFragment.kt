package com.example.xbcad7319_physiotherapyapp.ui.intake_forms

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.navigation.fragment.findNavController
import com.example.xbcad7319_physiotherapyapp.R


class IntakeFormsFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_intake_forms, container, false)

        // Initialize the ImageButton using the inflated view
        val ibtnHome: ImageButton = view.findViewById(R.id.ibtnHome)
        val btnForm1: Button  = view.findViewById(R.id.btnForm1)
        val btnForm2: Button  = view.findViewById(R.id.btnForm2)

        // Set OnClickListener for the Home button
        ibtnHome.setOnClickListener {
            findNavController().navigate(R.id.action_nav_intake_forms_to_nav_home_patient)
        }
        // Set OnClickListener for the Form 1 button
        btnForm1.setOnClickListener {
            findNavController().navigate(R.id.action_nav_intake_forms_to_nav_form1)
        }
        // Set OnClickListener for the Form 2 button
        btnForm2.setOnClickListener {
            findNavController().navigate(R.id.action_nav_intake_forms_to_nav_form2)
        }


        return view // Return the inflated view at the end
    }
}