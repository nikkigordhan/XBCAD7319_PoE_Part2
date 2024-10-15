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
        val ibtnAppointments: ImageButton = view.findViewById(R.id.ibtnBookAppointment)
        val ibtnMedicalHistory: ImageButton = view.findViewById(R.id.ibtnMedical_History)
        val ibtnMedicalTests: ImageButton = view.findViewById(R.id.ibtnMedical_Tests)
        val ibtnPatientProfile: ImageButton = view.findViewById(R.id.ibtnProfile)
        val ibtnNotifications: ImageButton = view.findViewById(R.id.ibtnNotifications)


        // Set OnClickListener for the Medical History button
        ibtnAppointments.setOnClickListener {
            findNavController().navigate(R.id.action_nav_home_patient_to_nav_app)
        }
        // Set OnClickListener for the Medical History button
        ibtnMedicalHistory.setOnClickListener {
            findNavController().navigate(R.id.action_nav_home_patient_to_nav_medical_history)
        }
        // Set OnClickListener for the Medical Tests button
        ibtnMedicalTests.setOnClickListener {
            findNavController().navigate(R.id.action_nav_home_patient_to_nav_medical_tests)
        }
        // Set OnClickListener for the Patient Profile button
        ibtnPatientProfile.setOnClickListener {
            findNavController().navigate(R.id.action_nav_home_patient_to_nav_patient_profile)
        }
        // Set OnClickListener for the Notifications button
        ibtnNotifications.setOnClickListener {
            findNavController().navigate(R.id.action_nav_home_patient_to_nav_notifications_patient)
        }



        return view // Return the inflated view
    }
}
