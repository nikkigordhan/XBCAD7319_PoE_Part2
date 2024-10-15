package com.example.xbcad7319_physiotherapyapp.ui.cancel_app_patient

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.navigation.fragment.findNavController
import com.example.xbcad7319_physiotherapyapp.R

class CancelAppPatientFragment : Fragment() {

    companion object {
        fun newInstance() = CancelAppPatientFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_cancel_app_patient, container, false)

        // Initialize the ImageButton using the inflated view
        val ibtnHome: ImageButton = view.findViewById(R.id.ibtnHome)

        // Set OnClickListener for the Home button
        ibtnHome.setOnClickListener {
            findNavController().navigate(R.id.action_nav_cancel_app_patient_to_nav_home_patient)
        }

        // add code logic here.

        return view // Return the inflated view at the end
    }
}
