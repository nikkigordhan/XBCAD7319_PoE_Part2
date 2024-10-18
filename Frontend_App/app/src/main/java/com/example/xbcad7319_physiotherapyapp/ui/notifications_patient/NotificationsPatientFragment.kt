package com.example.xbcad7319_physiotherapyapp.ui.notifications_patient

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import com.example.xbcad7319_physiotherapyapp.R
import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.xbcad7319_physiotherapyapp.ui.ApiClient
import com.example.xbcad7319_physiotherapyapp.ui.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.Log
import android.widget.ImageButton
import androidx.navigation.fragment.findNavController
import com.example.xbcad7319_physiotherapyapp.ui.Notification
import java.io.IOException

class NotificationsPatientFragment : Fragment() {

    private lateinit var listView: ListView
    private lateinit var notificationAdapter: ArrayAdapter<String>
    private val notificationList = mutableListOf<String>() // To hold notification data

    private val apiService: ApiService by lazy {
        ApiClient.getRetrofitInstance(requireContext()).create(ApiService::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notifications_patient, container, false)

        listView = view.findViewById(R.id.listNotifications)

        // Initialize the adapter and set it to the ListView
        notificationAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, notificationList)
        listView.adapter = notificationAdapter

        fetchNotifications()

        // Home button navigation
        val ibtnHome: ImageButton = view.findViewById(R.id.ibtnHome)
        ibtnHome.setOnClickListener {
            findNavController().navigate(R.id.action_nav_notifications_patient_to_home_patient)
        }

        return view
    }

    // Function to fetch notifications using the API
    private fun fetchNotifications() {
        // Retrieve the Bearer token from Shared Preferences
        val sharedPref = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val token = sharedPref.getString("bearerToken", null) // Replace with your token key

        if (token != null) {
            val call = apiService.getPatientNotifications("Bearer $token")

            call.enqueue(object : Callback<List<Notification>> {
                override fun onResponse(
                    call: Call<List<Notification>>,
                    response: Response<List<Notification>>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val notifications = response.body()!!
                        notificationList.clear()

                        // Populate notificationList with messages
                        notifications.forEach {
                            notificationList.add(it.message)
                        }

                        // Notify adapter to update the ListView
                        notificationAdapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(requireContext(), "Failed to fetch notifications", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<Notification>>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }
}


