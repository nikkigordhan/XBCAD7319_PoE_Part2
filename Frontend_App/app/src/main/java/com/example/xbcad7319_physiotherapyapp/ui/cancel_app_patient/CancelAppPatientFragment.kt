package com.example.xbcad7319_physiotherapyapp.ui.cancel_app_patient

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.xbcad7319_physiotherapyapp.R
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ListView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.xbcad7319_physiotherapyapp.ui.ApiClient
import com.example.xbcad7319_physiotherapyapp.ui.ApiService
import com.example.xbcad7319_physiotherapyapp.ui.AppointmentDetails
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException


class CancelAppPatientFragment : Fragment() {

    private lateinit var listAppointments: ListView
    private lateinit var btnCancel: Button
    private lateinit var btnSave: Button
    private var selectedAppointmentId: String = ""

    // Create an instance of ApiService using the updated ApiClient
    private val apiService: ApiService by lazy {
        ApiClient.getRetrofitInstance(requireContext()).create(ApiService::class.java) // Use requireContext()
    }

    private val TAG = "CancelAppPatientFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_cancel_app_patient, container, false)

        // Initialize views
        listAppointments = view.findViewById(R.id.listAppointments)
        btnCancel = view.findViewById(R.id.btnCancel)
        btnSave = view.findViewById(R.id.btnSave)

        // Handle appointment list selection
        listAppointments.setOnItemClickListener { _, _, position, _ ->
            // Assuming you have a list of appointments populated, get the selected appointment ID
            val appointment = listAppointments.adapter.getItem(position) as AppointmentDetails
            selectedAppointmentId = appointment.id // Replace with the actual property
        }

        // Set up the cancel button click listener
        btnCancel.setOnClickListener {
            if (selectedAppointmentId.isNotEmpty()) {
                cancelAppointment(selectedAppointmentId)
            } else {
                Toast.makeText(requireContext(), "Please select an appointment to cancel", Toast.LENGTH_SHORT).show()
            }
        }

        // Home button navigation
        val ibtnHome: ImageButton = view.findViewById(R.id.ibtnHome)
        ibtnHome.setOnClickListener {
            findNavController().navigate(R.id.action_nav_cancel_app_patient_to_nav_home_patient)
        }

        return view
    }

    // Cancel the selected appointment
    private fun cancelAppointment(appointmentId: String) {
        val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." // Replace with real token retrieval

        if (token.isNotEmpty()) {
            val call = apiService.cancelAppointment("Bearer $token", appointmentId)

            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Appointment canceled successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        val errorResponse = response.errorBody()?.string() ?: "Unknown error"
                        Log.e(TAG, "Failed to cancel appointment: HTTP ${response.code()} - $errorResponse")
                        Toast.makeText(requireContext(), "Failed to cancel: $errorResponse", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e(TAG, "Network error while canceling appointment: ${t.message}", t)
                    if (t is IOException) {
                        Toast.makeText(requireContext(), "Network error. Please check your connection.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "An unexpected error occurred: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        } else {
            Log.e(TAG, "Authorization token is missing")
        }
    }
}

