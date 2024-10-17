package com.example.xbcad7319_physiotherapyapp.ui.reschedule_app_patient

import android.content.Context
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
import com.example.xbcad7319_physiotherapyapp.R
import com.example.xbcad7319_physiotherapyapp.ui.ApiClient
import com.example.xbcad7319_physiotherapyapp.ui.ApiService
import com.example.xbcad7319_physiotherapyapp.ui.AppointmentDetails
import com.example.xbcad7319_physiotherapyapp.ui.RescheduleAppointmentRequest
import com.example.xbcad7319_physiotherapyapp.ui.RescheduleAppointmentResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.Calendar

class RescheduleAppPatientFragment : Fragment() {

    private lateinit var calendarView: CalendarView
    private lateinit var timeSpinner: Spinner
    private lateinit var descriptionEditText: EditText
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button

    private var selectedDate: Long = 0L // Will hold timestamp for the selected date
    private var appointmentId: String = "" // Assume this comes from somewhere, passed in as an argument

    // Create an instance of ApiService using the updated ApiClient
    private val apiService: ApiService by lazy {
        ApiClient.getRetrofitInstance(requireContext()).create(ApiService::class.java)
    }

    private val TAG = "RescheduleAppPatientFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reschedule_app_patient, container, false)

        // Initialize views
        calendarView = view.findViewById(R.id.calendarView)
        timeSpinner = view.findViewById(R.id.spinner2)
        descriptionEditText = view.findViewById(R.id.etxtDescription)
        btnSave = view.findViewById(R.id.btnSave)
        btnCancel = view.findViewById(R.id.btnCancel)

        // Populate the time spinner with times from 8:00 AM to 4:30 PM every 30 minutes
        populateTimeSpinner()

        // Get the selected date from the CalendarView
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            selectedDate = calendar.timeInMillis // Save selected date as timestamp
        }

        // Save button click listener
        btnSave.setOnClickListener {
            val selectedTime = timeSpinner.selectedItem.toString()
            val description = descriptionEditText.text.toString()

            if (selectedDate != 0L && selectedTime.isNotEmpty() && description.isNotEmpty()) {
                // Retrieve the logged-in user's userId
                val sharedPref = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
                val userId = sharedPref.getString("userId", null)

                if (userId != null) {
                    // Create a RescheduleAppointmentRequest with the userId
                    val rescheduleRequest = RescheduleAppointmentRequest(
                        date = selectedDate,  // Timestamp of selected date
                        time = selectedTime,
                        description = description
                    )
                    rescheduleAppointment(rescheduleRequest)
                } else {
                    Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
                }
            } else {
                val message = when {
                    selectedDate == 0L -> "Please select a date"
                    selectedTime.isEmpty() -> "Please select a time"
                    description.isEmpty() -> "Please enter a description"
                    else -> "Please fill in all fields"
                }
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun populateTimeSpinner() {
        val times = mutableListOf<String>()
        var hour = 8
        var minute = 0

        while (hour < 17 || (hour == 17 && minute == 0)) {
            val time = String.format("%02d:%02d %s", hour % 12, minute, if (hour < 12) "AM" else "PM")
            times.add(time)
            minute += 30
            if (minute >= 60) {
                minute = 0
                hour++
            }
        }

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, times)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        timeSpinner.adapter = adapter
    }

    private fun rescheduleAppointment(rescheduleRequest: RescheduleAppointmentRequest) {
        val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." // Replace with real token retrieval

        if (token.isNotEmpty()) {
            val call = apiService.rescheduleAppointment("Bearer $token", rescheduleRequest)

            call.enqueue(object : Callback<RescheduleAppointmentResponse> {
                override fun onResponse(
                    call: Call<RescheduleAppointmentResponse>,
                    response: Response<RescheduleAppointmentResponse>
                ) {
                    if (response.isSuccessful) {
                        val rescheduleResponse = response.body()
                        rescheduleResponse?.let {
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()

                        }
                    } else {
                        val errorResponse = response.errorBody()?.string() ?: "Unknown error"
                        Log.e(TAG, "Failed to reschedule appointment: HTTP ${response.code()} - $errorResponse")
                        Toast.makeText(requireContext(), "Failed to reschedule: $errorResponse", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<RescheduleAppointmentResponse>, t: Throwable) {
                    Log.e(TAG, "Network error while rescheduling appointment: ${t.message}", t)
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




