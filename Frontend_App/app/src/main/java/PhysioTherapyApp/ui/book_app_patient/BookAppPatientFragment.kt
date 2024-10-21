package com.example.PhysioTherapyApp.ui.book_app_patient

import android.content.Context
import android.content.SharedPreferences
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
import com.example.PhysioTherapyApp.R
import com.example.PhysioTherapyApp.ui.ApiClient
import com.example.PhysioTherapyApp.ui.ApiService
import com.example.PhysioTherapyApp.ui.BookAppointmentRequest
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.Log
import org.json.JSONException
import org.json.JSONObject

class BookAppPatientFragment : Fragment() {

    private lateinit var calendarView: CalendarView
    private lateinit var timeSpinner: Spinner
    private lateinit var descriptionEditText: EditText
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button
    private lateinit var sharedPref: SharedPreferences

    private var selectedDate: String = ""

    // Create an instance of ApiService
    private val apiService: ApiService by lazy {
        ApiClient.getRetrofitInstance(requireContext()).create(ApiService::class.java)
    }

    private val TAG = "BookAppPatientFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_book_app_patient, container, false)

        // Initialize SharedPreferences
        sharedPref = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE)

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
                // Retrieve the JSON response from SharedPreferences
                val tokenResponse = sharedPref.getString("bearerToken", null)
                val username = sharedPref.getString("loggedInUsername", null)

                Log.e(TAG, "Token response = $tokenResponse")

                if (tokenResponse != null && username != null) {
                    try {
                        // Parse the JSON response to extract the token
                        val jsonObject = JSONObject(tokenResponse)
                        val token = jsonObject.getString("token")

                        // Create an AppointmentRequest with the necessary details
                        val appointmentRequest = BookAppointmentRequest(
                            patient = username,  // Use the stored username
                            date = selectedDate,
                            time = selectedTime,
                            description = description
                        )
                        // Pass the extracted token
                        bookAppointment(appointmentRequest, token)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Toast.makeText(requireContext(), "Error parsing token", Toast.LENGTH_SHORT).show()
                    }
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


    private fun bookAppointment(appointmentRequest: BookAppointmentRequest, token: String) {
        // Ensure the Bearer prefix is included
        val call = apiService.bookAppointment("Bearer $token", appointmentRequest)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Appointment booked successfully", Toast.LENGTH_SHORT).show()
                    Log.e(TAG, "Appointment booked successfully")
                    findNavController().navigate(R.id.action_nav_book_app_patient_to_nav_home_patient)
                } else {
                    // Logging and error handling
                    val errorResponse = response.errorBody()?.string() ?: "Unknown error"
                    Log.e(TAG, "Failed to book appointment: HTTP ${response.code()} - $errorResponse")
                    Toast.makeText(requireContext(), "Failed to book appointment: $errorResponse", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e(TAG, "Network error while booking appointment: ${t.message}", t)
                Toast.makeText(requireContext(), "Network error. Please check your connection.", Toast.LENGTH_SHORT).show()
            }
        })
    }

}





