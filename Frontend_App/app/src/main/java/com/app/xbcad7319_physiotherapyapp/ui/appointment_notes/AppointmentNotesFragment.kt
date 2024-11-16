package com.example.xbcad7319_physiotherapyapp.ui.appointment_notes

import com.example.xbcad7319_physiotherapyapp.ui.ApiClient
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.navigation.fragment.findNavController
import com.example.xbcad7319_physiotherapyapp.R
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.*
import com.example.xbcad7319_physiotherapyapp.ui.ApiService
import com.example.xbcad7319_physiotherapyapp.ui.AppointmentDetails
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class AppointmentNotesFragment : Fragment() {

    private lateinit var listAppointments: ListView
    private lateinit var btnCancel: Button
    private lateinit var btnSave: Button
    private var selectedAppointmentId: String = ""
    private lateinit var sharedPref: SharedPreferences
    private lateinit var etxtNotes: EditText

    private val apiService: ApiService by lazy {
        ApiClient.getRetrofitInstance(requireContext()).create(ApiService::class.java)
    }

    private val TAG = "AppointmentNotesFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_appointment_notes, container, false)

        sharedPref = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE)

        listAppointments = view.findViewById(R.id.listAppointments)
        btnCancel = view.findViewById(R.id.btnCancel)
        btnSave = view.findViewById(R.id.btnSave)
        etxtNotes = view.findViewById(R.id.etxtNotes)

        // Set up ListView item click listener to select an appointment
        listAppointments.setOnItemClickListener { _, _, position, _ ->
            val appointment = listAppointments.adapter.getItem(position) as AppointmentDetails
            selectedAppointmentId = appointment.id
            Log.d(TAG, "Selected appointment ID: $selectedAppointmentId")
            showToast("Selected Appointment: ${selectedAppointmentId}")
        }

        // Set up the cancel button click listener
        btnCancel.setOnClickListener {
            findNavController().navigate(R.id.action_nav_app_notes_to_nav_home_staff)
        }

        // Set up the save button click listener
        btnSave.setOnClickListener {
            if (selectedAppointmentId.isNotEmpty()) {
                val notes = etxtNotes.text.toString()
                addAppointmentNotes(selectedAppointmentId, notes)
            } else {
                Toast.makeText(requireContext(), "Please select an appointment to add notes", Toast.LENGTH_SHORT).show()
            }
        }

        fetchAndDisplayAppointments()

        // Home button navigation
        val ibtnHome: ImageButton = view.findViewById(R.id.ibtnHome)
        ibtnHome.setOnClickListener {
            findNavController().navigate(R.id.action_nav_app_notes_to_nav_home_staff)
        }
        return view
    }

    private fun fetchAndDisplayAppointments() {
        val tokenResponse = sharedPref.getString("bearerToken", null)

        tokenResponse?.let {
            try {
                val jsonObject = JSONObject(it)
                val token = jsonObject.getString("token")

                val call = apiService.getAllConfirmedAppointments("Bearer $token")
                call.enqueue(object : Callback<List<AppointmentDetails>> {
                    override fun onResponse(call: Call<List<AppointmentDetails>>, response: Response<List<AppointmentDetails>>) {
                        if (response.isSuccessful) {
                            response.body()?.let { appointments ->
                                populateListView(appointments)
                            } ?: Log.d(TAG, "No confirmed appointments found")
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

    private fun addAppointmentNotes(appointmentId: String, notes: String) {
        Log.d(TAG, "Adding appointment notes for ID: $appointmentId, Notes: $notes")

        val tokenResponse = sharedPref.getString("bearerToken", null)
        Log.d(TAG, "Token Response: $tokenResponse")

        tokenResponse?.let {
            try {
                val jsonObject = JSONObject(it)
                val token = jsonObject.getString("token")
                Log.d(TAG, "Extracted Token: $token")

                // Create a map for the request body
                val requestBody = mapOf("notes" to notes)

                val call = apiService.addAppointmentNotes("Bearer $token", appointmentId, requestBody)

                Log.d(TAG, "Making API call to add notes...")
                call.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful) {
                            Log.d(TAG, "Notes added successfully, Response Code: ${response.code()}")
                            Toast.makeText(requireContext(), "Notes added successfully", Toast.LENGTH_SHORT).show()
                            etxtNotes.text.clear() // Clear notes input field
                            fetchAndDisplayAppointments() // Refresh appointment list
                            findNavController().navigate(R.id.action_nav_app_notes_to_nav_home_staff)
                        } else {
                            val errorResponse = response.errorBody()?.string() ?: "Unknown error"
                            Log.e(TAG, "Failed to add notes: HTTP ${response.code()} - $errorResponse")
                            Toast.makeText(requireContext(), "Failed to add notes: $errorResponse", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.e(TAG, "Network error while adding notes: ${t.message}", t)
                        if (t is IOException) {
                            Toast.makeText(requireContext(), "Network error. Please check your connection.", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(requireContext(), "An unexpected error occurred: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
            } catch (e: JSONException) {
                Log.e(TAG, "Error parsing token: ${e.message}")
                showToast("Error adding notes")
            }
        } ?: Log.d(TAG, "Token is null, user not logged in.")
    }



    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}


