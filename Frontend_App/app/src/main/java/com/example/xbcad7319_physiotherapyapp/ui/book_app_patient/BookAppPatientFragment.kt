package com.example.xbcad7319_physiotherapyapp.ui.book_app_patient

import android.content.Context
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.xbcad7319_physiotherapyapp.R
import com.example.xbcad7319_physiotherapyapp.ui.ApiClient
import com.example.xbcad7319_physiotherapyapp.ui.ApiService
import com.example.xbcad7319_physiotherapyapp.ui.BookAppointmentRequest
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.Log
import java.io.IOException

class BookAppPatientFragment : Fragment() {

    private lateinit var calendarView: CalendarView
    private lateinit var timeSpinner: Spinner
    private lateinit var descriptionEditText: EditText
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button

    private var selectedDate: String = ""

    // Create an instance of ApiService using the updated ApiClient
    private val apiService: ApiService by lazy {
        ApiClient.getRetrofitInstance(requireContext()).create(ApiService::class.java) // Use requireContext()
    }

    private val TAG = "BookAppPatientFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       val view = inflater.inflate(R.layout.fragment_book_app_patient, container, false)

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
            selectedDate = "$year-${month + 1}-$dayOfMonth"

        }

        // Save button click listener
        btnSave.setOnClickListener {
            val selectedTime = timeSpinner.selectedItem.toString()
            val description = descriptionEditText.text.toString()

            if (selectedDate.isNotEmpty() && selectedTime.isNotEmpty() && description.isNotEmpty()) {
                // Retrieve the logged-in user's userId
                val sharedPref = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
                val userId = "6709196e9bb6e3e845d1d159" //sharedPref.getString("userId", null) // Get userId from shared preferences

                if (userId != null) {
                    // Create an AppointmentRequest with the userId
                    val appointmentRequest = BookAppointmentRequest(
                        patient = userId,
                        date = selectedDate,
                        time = selectedTime,
                        description = description
                    )
                   bookAppointment(appointmentRequest)
                } else {
                    Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Display appropriate messages based on empty fields
                val message = when {
                    selectedDate.isEmpty() -> "Please select a date"
                    selectedTime.isEmpty() -> "Please select a time"
                    description.isEmpty() -> "Please enter a description"
                    else -> "Please fill in all fields"
                }
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }

        // Home button navigation
        val ibtnHome: ImageButton = view.findViewById(R.id.ibtnHome)
        ibtnHome.setOnClickListener {
            findNavController().navigate(R.id.action_nav_book_app_patient_to_nav_home_patient)
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

    private fun bookAppointment(appointmentRequest: BookAppointmentRequest) {
        // Retrieve the Bearer token from Shared Preferences or your authentication manager
        val sharedPref = "670d375b32eb2e7855bd897a"
        val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjY3MDkxOTZlOWJiNmUzZTg0NWQxZDE1OSIsInJvbGUiOiJwYXRpZW50IiwiaWF0IjoxNzI5MTUxNjQwLCJleHAiOjE3MjkxNTUyNDB9.02Xo-Cxyl3_Ojvax3Cq3mEFk68Bu5GKt1zJ781Q9JSY" //sharedPref.getString("bearerToken", null) // Replace with your token key

        if (token.isNotEmpty()) {
            val call = apiService.bookAppointment("Bearer $token", appointmentRequest) // Pass the token

            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Appointment booked successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        // Log additional response information
                        val errorResponse = response.errorBody()?.string() ?: "Unknown error"
                        Log.e(TAG, "Failed to book appointment: HTTP ${response.code()} - $errorResponse")

                        // Additional information to log
                        Log.e(TAG, "Response Headers: ${response.headers()}")
                        Log.e(TAG, "Request URL: ${call.request().url}")
                        Log.e(TAG, "Request Method: ${call.request().method}")

                        Toast.makeText(requireContext(), "Failed to book appointment: $errorResponse", Toast.LENGTH_SHORT).show()
                    }
                }

                // Handle failure in the request
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    // Log the error message and exception
                    Log.e(TAG, "Network error while booking appointment: ${t.message}", t)

                    // Check if it is a network issue
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




