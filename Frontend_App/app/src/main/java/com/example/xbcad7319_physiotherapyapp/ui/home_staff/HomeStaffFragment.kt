package com.example.xbcad7319_physiotherapyapp.ui.home_staff

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.navigation.fragment.findNavController
import com.example.xbcad7319_physiotherapyapp.R

class HomeStaffFragment : Fragment() {

    companion object {
        fun newInstance() = HomeStaffFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_home_staff, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the ImageButton using the view
        val ibtnNotifications: ImageButton = view.findViewById(R.id.ibtnNotifications)
        val ibtnPatientNotes: ImageButton = view.findViewById(R.id.ibtnPatient_Notes)
        val ibtnViewPatientProfile: ImageButton = view.findViewById(R.id.ibtnPatient_Profile)
        val ibtnBilling: ImageButton = view.findViewById(R.id.ibtnBilling)
        val ibtnApp: ImageButton = view.findViewById(R.id.ibtnAppointments)

        // Set OnClickListener for the Notifications button
        ibtnNotifications.setOnClickListener {
            findNavController().navigate(R.id.action_nav_home_staff_to_nav_notifications_staff)
        }
        // Set OnClickListener for the Patient Notes button
        ibtnPatientNotes.setOnClickListener {
            findNavController().navigate(R.id.action_nav_home_staff_to_nav_app_notes)
        }
        // Set OnClickListener for the Patient Profile button
        ibtnViewPatientProfile.setOnClickListener {
            findNavController().navigate(R.id.action_nav_home_staff_to_nav_view_patient_profile)
        }
        // Set OnClickListener for the Billing button
        ibtnBilling.setOnClickListener {
            findNavController().navigate(R.id.action_nav_home_staff_to_nav_billing)
        }
        // Set OnClickListener for the Appointment button
        ibtnApp.setOnClickListener {
            findNavController().navigate(R.id.action_nav_home_staff_to_nav_app_staff)
        }
    }
}
