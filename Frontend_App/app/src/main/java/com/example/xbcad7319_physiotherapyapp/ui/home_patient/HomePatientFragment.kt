package com.example.xbcad7319_physiotherapyapp.ui.home_patient

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.navigation.fragment.findNavController
import com.example.xbcad7319_physiotherapyapp.R

class HomePatientFragment : Fragment() {

    companion object {
        fun newInstance() = HomePatientFragment()
    }

    private val viewModel: HomePatientViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO: Use the ViewModel if needed
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home_patient, container, false)

        // Initialize the ImageButton using the inflated view
        val ibtnMedicalHistory: ImageButton = view.findViewById(R.id.ibtnMedical_History)

        // Set OnClickListener for the Medical History button
        ibtnMedicalHistory.setOnClickListener {
            findNavController().navigate(R.id.action_nav_home_patient_to_medicalHistoryFragment)
        }

        return view // Return the inflated view
    }
}
