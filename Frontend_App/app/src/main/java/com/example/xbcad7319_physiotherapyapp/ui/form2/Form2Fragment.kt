package com.example.xbcad7319_physiotherapyapp.ui.form2

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.navigation.fragment.findNavController
import com.example.xbcad7319_physiotherapyapp.R

class Form2Fragment : Fragment() {

    companion object {
        fun newInstance() = Form2Fragment()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_form2, container, false) // Corrected to fragment_form1

        // Initialize the ImageButton using the inflated view
        val ibtnBack: ImageButton = view.findViewById(R.id.ibtnBack)

        // Set OnClickListener for the Back button
        ibtnBack.setOnClickListener {
            findNavController().navigate(R.id.action_nav_form2_to_nav_intake_forms)
        }

        // Return the inflated view
        return view
    }
}