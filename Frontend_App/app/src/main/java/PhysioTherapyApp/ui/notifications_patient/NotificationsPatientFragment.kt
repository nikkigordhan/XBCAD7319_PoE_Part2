package com.example.PhysioTherapyApp.ui.notifications_patient

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.example.PhysioTherapyApp.R
import android.content.Context
import android.widget.Toast
import com.example.PhysioTherapyApp.ui.ApiClient
import com.example.PhysioTherapyApp.ui.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ImageButton
import androidx.navigation.fragment.findNavController
import com.example.PhysioTherapyApp.ui.NotificationsResponse
import org.json.JSONException
import org.json.JSONObject

class NotificationsPatientFragment : Fragment() {

    private lateinit var listView: ListView
    private lateinit var notificationAdapter: ArrayAdapter<String>
    private val notificationList = mutableListOf<String>() // To hold notification data

    private val apiService: ApiService by lazy {
        ApiClient.getRetrofitInstance(requireContext()).create(ApiService::class.java)
    }

    private val TAG = "NotificationsPatientFragment" // Log tag for logging

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notifications_patient, container, false)

        listView = view.findViewById(R.id.listNotifications)

        // Initialize the adapter and set it to the ListView
        notificationAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, notificationList)
        listView.adapter = notificationAdapter

        // Fetch notifications from the server
        fetchNotifications()

        // Home button navigation
        val ibtnHome: ImageButton = view.findViewById(R.id.ibtnHome)
        ibtnHome.setOnClickListener {
            findNavController().navigate(R.id.action_nav_notifications_patient_to_home_patient)
        }

        return view
    }

    private fun fetchNotifications() {
        // Retrieve the Bearer token from Shared Preferences
        val sharedPref = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val tokenResponse = sharedPref.getString("bearerToken", null) // Replace with your token key

        tokenResponse?.let {
            try {
                val jsonObject = JSONObject(it)
                val token = jsonObject.getString("token")

                // Call the API to get patient notifications
                val call = apiService.getPatientNotifications("Bearer $token")

                call.enqueue(object : Callback<NotificationsResponse> {
                    override fun onResponse(
                        call: Call<NotificationsResponse>,
                        response: Response<NotificationsResponse>
                    ) {
                        if (response.isSuccessful && response.body() != null) {
                            val notifications = response.body()!!.notifications
                            notificationList.clear()

                            // Populate notificationList with formatted strings
                            notifications.forEach { notification ->
                                val datePart = notification.date.split("T")[0] // Format date
                                val timePart = notification.time // Adjust if necessary to extract time
                                val description = notification.message // Or whatever description you want to show

                                // Format the string as required
                                val formattedNotification = "Date: $datePart\nTime: $timePart\nDescription: $description"
                                notificationList.add(formattedNotification)
                            }

                            // Notify adapter to update the ListView
                            notificationAdapter.notifyDataSetChanged()
                        } else {
                            Log.e(TAG, "Failed to fetch notifications: ${response.errorBody()?.string()}")
                            Toast.makeText(requireContext(), "Failed to fetch notifications", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<NotificationsResponse>, t: Throwable) {
                        Log.e(TAG, "Error fetching notifications: ${t.message}", t)
                        Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            } catch (e: JSONException) {
                Log.e(TAG, "Error parsing token: ${e.message}")
                Toast.makeText(requireContext(), "Error fetching notifications", Toast.LENGTH_SHORT).show()
            }
        } ?: Log.d(TAG, "Token is null, user not logged in.")
    }

}



