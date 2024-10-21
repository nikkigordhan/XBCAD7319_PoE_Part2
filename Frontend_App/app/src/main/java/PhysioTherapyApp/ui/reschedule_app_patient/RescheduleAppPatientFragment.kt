package com.example.PhysioTherapyApp.ui.reschedule_app_patient

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import android.widget.Spinner
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.PhysioTherapyApp.R
import com.example.PhysioTherapyApp.ui.ApiClient
import com.example.PhysioTherapyApp.ui.ApiService
import com.example.PhysioTherapyApp.ui.AppointmentDetails
import com.example.PhysioTherapyApp.ui.RescheduleAppointmentRequest
import com.example.PhysioTherapyApp.ui.RescheduleAppointmentResponse
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class RescheduleAppPatientFragment : Fragment() {

    private lateinit var calendarView: CalendarView
    private lateinit var timeSpinner: Spinner
    private lateinit var descriptionEditText: EditText
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button
    private lateinit var appointmentListView: ListView
    private lateinit var sharedPref: SharedPreferences

    private var selectedDate: Long = 0L
    private var selectedAppointmentId: String? = null

    private val apiService: ApiService by lazy {
        ApiClient.getRetrofitInstance(requireContext()).create(ApiService::class.java)
    }

    private val TAG = "RescheduleAppPatientFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reschedule_app_patient, container, false)

        sharedPref = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE)

        calendarView = view.findViewById(R.id.calendarView)
        timeSpinner = view.findViewById(R.id.spinner2)
        descriptionEditText = view.findViewById(R.id.etxtDescription)
        btnSave = view.findViewById(R.id.btnSave)
        btnCancel = view.findViewById(R.id.btnCancel)
        appointmentListView = view.findViewById(R.id.listAppointments)

        populateTimeSpinner()
        fetchAndDisplayAppointments()

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            selectedDate = calendar.timeInMillis
            Log.d(TAG, "Selected date: $selectedDate")
        }

        btnSave.setOnClickListener {
            val selectedTime = timeSpinner.selectedItem.toString()
            val description = descriptionEditText.text.toString()

            if (validateInputs(selectedTime, description)) {
                // Retrieve the JSON response from SharedPreferences
                val tokenResponse = sharedPref.getString("bearerToken", null)
                val username = sharedPref.getString("loggedInUsername", null)

                Log.e(TAG, "Token response = $tokenResponse")

                if (tokenResponse != null && username != null) {
                    try {
                        // Parse the JSON response to extract the token
                        val jsonObject = JSONObject(tokenResponse)
                        val token = jsonObject.getString("token")

                        val rescheduleRequest = RescheduleAppointmentRequest(
                            date = selectedDate,
                            time = selectedTime,
                            description = description
                        )
                        Log.d(TAG, "Rescheduling appointment with ID: $selectedAppointmentId")
                        rescheduleAppointment(selectedAppointmentId!!, rescheduleRequest, token)
                    } catch (e: JSONException) {
                        Log.e(TAG, "Error parsing token: ${e.message}")
                        showToast("Error parsing token")
                    }
                } else {
                    showToast("User not logged in")
                }
            }
        }


        view.findViewById<ImageButton>(R.id.ibtnHome).setOnClickListener {
            findNavController().navigate(R.id.action_nav_reschedule_app_patient_to_nav_home_patient)
        }

        return view
    }

    private fun validateInputs(selectedTime: String, description: String): Boolean {
        return when {
            selectedAppointmentId == null -> {
                showToast("Please select an appointment")
                false
            }
            selectedDate == 0L -> {
                showToast("Please select a date")
                false
            }
            selectedTime.isEmpty() -> {
                showToast("Please select a time")
                false
            }
            description.isEmpty() -> {
                showToast("Please enter a description")
                false
            }
            else -> true
        }
    }

    private fun fetchAndDisplayAppointments() {
        val tokenResponse = sharedPref.getString("bearerToken", null)

        tokenResponse?.let {
            try {
                val jsonObject = JSONObject(it)
                val token = jsonObject.getString("token")

                val call = apiService.getConfirmedAppointments("Bearer $token")
                call.enqueue(object : Callback<List<AppointmentDetails>> {
                    override fun onResponse(call: Call<List<AppointmentDetails>>, response: Response<List<AppointmentDetails>>) {
                        if (response.isSuccessful) {
                            response.body()?.let {
                                populateListView(it)

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
        // Map appointment details including the ID for display
        val appointmentDescriptions = appointments.map { appointment ->
            // Extract the date part from the date string
            val datePart = appointment.date.split("T")[0] // This will give you '2024-10-22'

            "Date: $datePart\nTime: ${appointment.time}\nDescription: ${appointment.description}"
        }
        Log.d(TAG, "Appointments for ListView: $appointmentDescriptions")

        // Set the adapter with the updated appointment list
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, appointmentDescriptions)
        appointmentListView.adapter = adapter

        // Add click listener to list items
        appointmentListView.setOnItemClickListener { parent, view, position, id ->
            val selectedAppointment = appointments[position]
            selectedAppointmentId = selectedAppointment.id  // Store the selected appointment ID
            Log.d(TAG, "Selected appointment ID: $selectedAppointmentId")
            showToast("Selected Appointment: ${selectedAppointment.id}")
        }
    }




    private fun populateTimeSpinner() {
        val times = listOf(
            "8:00 AM", "8:30 AM", "9:00 AM", "9:30 AM", "10:00 AM",
            "10:30 AM", "11:00 AM", "11:30 AM", "12:00 PM", "12:30 PM",
            "1:00 PM", "1:30 PM", "2:00 PM", "2:30 PM", "3:00 PM",
            "3:30 PM", "4:00 PM", "4:30 PM"
        )

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, times)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        timeSpinner.adapter = adapter
    }


    private fun rescheduleAppointment(appointmentId: String, rescheduleRequest: RescheduleAppointmentRequest, token: String) {
        // Create a call to the API with the token in the header
        val call = apiService.rescheduleAppointment("Bearer $token", appointmentId, rescheduleRequest)

        call.enqueue(object : Callback<RescheduleAppointmentResponse> {
            override fun onResponse(call: Call<RescheduleAppointmentResponse>, response: Response<RescheduleAppointmentResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        Log.d(TAG, "Appointment rescheduled: ${it.message}")
                        showToast(it.message)
                        findNavController().navigate(R.id.action_nav_reschedule_app_patient_to_nav_home_patient)
                    }
                } else {
                    Log.e(TAG, "Failed to reschedule appointment: ${response.errorBody()?.string()}")
                    showToast("Failed to reschedule appointment")
                }
            }

            override fun onFailure(call: Call<RescheduleAppointmentResponse>, t: Throwable) {
                Log.e(TAG, "Network error during rescheduling: ${t.message}")
                showToast("Network error")
            }
        })
    }


    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}


