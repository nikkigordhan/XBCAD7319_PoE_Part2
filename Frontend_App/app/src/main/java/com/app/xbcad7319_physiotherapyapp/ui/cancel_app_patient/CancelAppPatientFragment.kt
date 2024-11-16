package com.example.xbcad7319_physiotherapyapp.ui.cancel_app_patient

import com.example.xbcad7319_physiotherapyapp.ui.ApiClient
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.xbcad7319_physiotherapyapp.R
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ListView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.xbcad7319_physiotherapyapp.ui.ApiService
import com.example.xbcad7319_physiotherapyapp.ui.AppointmentDetails
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException


class CancelAppPatientFragment : Fragment() {

    private lateinit var listAppointments: ListView
    private lateinit var btnCancel: Button
    private lateinit var btnSave: Button
    private var selectedAppointmentId: String = ""
    private lateinit var sharedPref: SharedPreferences

    private val apiService: ApiService by lazy {
        ApiClient.getRetrofitInstance(requireContext()).create(ApiService::class.java)
    }

    private val TAG = "CancelAppPatientFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_cancel_app_patient, container, false)

        sharedPref = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE)

        listAppointments = view.findViewById(R.id.listAppointments)
        btnCancel = view.findViewById(R.id.btnCancel)
        btnSave = view.findViewById(R.id.btnSave)

        listAppointments.setOnItemClickListener { _, _, position, _ ->
            val appointment = listAppointments.adapter.getItem(position) as AppointmentDetails
            selectedAppointmentId = appointment.id
        }

        // Set up the cancel button click listener
        btnCancel.setOnClickListener {
            if (selectedAppointmentId.isNotEmpty()) {
                cancelAppointment(selectedAppointmentId)
            } else {
                Toast.makeText(requireContext(), "Please select an appointment to cancel", Toast.LENGTH_SHORT).show()
            }
        }

        // Set up the save button click listener
        btnSave.setOnClickListener {
            if (selectedAppointmentId.isNotEmpty()) {
                cancelAppointment(selectedAppointmentId)
            } else {
                Toast.makeText(requireContext(), "Please select an appointment to cancel", Toast.LENGTH_SHORT).show()
            }
        }

        fetchAndDisplayAppointments()

        val ibtnHome: ImageButton = view.findViewById(R.id.ibtnHome)
        ibtnHome.setOnClickListener {
            findNavController().navigate(R.id.action_nav_cancel_app_patient_to_nav_home_patient)
        }

        return view
    }

    private fun fetchAndDisplayAppointments() {
        val tokenResponse = sharedPref.getString("bearerToken", null)

        tokenResponse?.let {
            try {
                val jsonObject = JSONObject(it)
                val token = jsonObject.getString("token")

                val call = apiService.getAllAppointments("Bearer $token")
                call.enqueue(object : Callback<List<AppointmentDetails>> {
                    override fun onResponse(call: Call<List<AppointmentDetails>>, response: Response<List<AppointmentDetails>>) {
                        if (response.isSuccessful) {
                            response.body()?.let { appointments ->
                                populateListView(appointments)
                            } ?: Log.d(TAG, "No appointments found")
                        } else {
                            Log.e(TAG, "Failed to load appointments: ${response.errorBody()?.string()}")
                            showToast("Failed to load appointments")
                        }
                    }

                    override fun onFailure(call: Call<List<AppointmentDetails>>, t: Throwable) {
                        Log.e(TAG, "Network error: ${t.message}")
                        showToast("Network error")
                    }
                })
            } catch (e: JSONException) {
                Log.e(TAG, "Error parsing token: ${e.message}")
                showToast("Error fetching appointments")
            }
        } ?: Log.d(TAG, "Token is null, user not logged in.")
    }

    private fun populateListView(appointments: List<AppointmentDetails>) {
        val appointmentDescriptions = appointments.map { appointment ->
            val datePart = appointment.date.split("T")[0]
            "Date: $datePart\nTime: ${appointment.time}\nDescription: ${appointment.description}"
        }
        Log.d(TAG, "Appointments for ListView: $appointmentDescriptions")

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, appointmentDescriptions)
        listAppointments.adapter = adapter

        listAppointments.setOnItemClickListener { _, _, position, _ ->
            val selectedAppointment = appointments[position]
            selectedAppointmentId = selectedAppointment.id
            Log.d(TAG, "Selected appointment ID: $selectedAppointmentId")
            showToast("Selected Appointment: ${selectedAppointment.id}")
        }
    }

    private fun cancelAppointment(appointmentId: String) {
        val tokenResponse = sharedPref.getString("bearerToken", null)

        tokenResponse?.let {
            try {
                val jsonObject = JSONObject(it)
                val token = jsonObject.getString("token")

                val call = apiService.cancelAppointment("Bearer $token", appointmentId)

                call.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful) {
                            Toast.makeText(requireContext(), "Appointment canceled successfully", Toast.LENGTH_SHORT).show()
                            findNavController().navigate(R.id.action_nav_cancel_app_patient_to_nav_home_patient)
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
            } catch (e: JSONException) {
                Log.e(TAG, "Error parsing token: ${e.message}")
                showToast("Error canceling appointment")
            }
        } ?: Log.d(TAG, "Token is null, user not logged in.")
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}



