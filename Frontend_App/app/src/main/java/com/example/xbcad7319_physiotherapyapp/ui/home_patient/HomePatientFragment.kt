package com.example.xbcad7319_physiotherapyapp.ui.home_patient

import android.content.Context
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.example.xbcad7319_physiotherapyapp.R
import com.example.xbcad7319_physiotherapyapp.ui.ApiClient
import com.example.xbcad7319_physiotherapyapp.ui.ApiService
import com.example.xbcad7319_physiotherapyapp.ui.Notification
import com.example.xbcad7319_physiotherapyapp.ui.NotificationsResponse
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomePatientFragment : Fragment() {

    companion object {
        fun newInstance() = HomePatientFragment()
    }

    private val viewModel: HomePatientViewModel by viewModels()
    private lateinit var txtNotificationCount: TextView
    private val notificationList = mutableListOf<Notification>()
    private val TAG = "HomePatientFragment"

    private val apiService: ApiService by lazy {
        ApiClient.getRetrofitInstance(requireContext()).create(ApiService::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView called")
        val view = inflater.inflate(R.layout.fragment_home_patient, container, false)

        // Initialize TextView for notification count
        txtNotificationCount = view.findViewById(R.id.txtNotificationCount)
        Log.d(TAG, "TextView for notification count initialized: $txtNotificationCount")

        // Load notifications
        loadPatientNotifications()

        // Initialize buttons and set OnClickListeners
        val ibtnAppointments: ImageButton = view.findViewById(R.id.ibtnBookAppointment)
        val ibtnMedicalHistory: ImageButton = view.findViewById(R.id.ibtnMedical_History)
        val ibtnMedicalTests: ImageButton = view.findViewById(R.id.ibtnMedical_Tests)
        val ibtnPatientProfile: ImageButton = view.findViewById(R.id.ibtnProfile)
        val ibtnNotifications: ImageButton = view.findViewById(R.id.ibtnNotifications)
        val ibtnIntakeForms: ImageButton = view.findViewById(R.id.ibtnIntake_Forms)

        ibtnAppointments.setOnClickListener {
            findNavController().navigate(R.id.action_nav_home_patient_to_nav_app)
        }
        ibtnMedicalHistory.setOnClickListener {
            findNavController().navigate(R.id.action_nav_home_patient_to_nav_medical_history)
        }
        ibtnMedicalTests.setOnClickListener {
            findNavController().navigate(R.id.action_nav_home_patient_to_nav_medical_tests)
        }
        ibtnPatientProfile.setOnClickListener {
            findNavController().navigate(R.id.action_nav_home_patient_to_nav_patient_profile)
        }
        ibtnNotifications.setOnClickListener {
            findNavController().navigate(R.id.action_nav_home_patient_to_nav_notifications_patient)
        }
        ibtnIntakeForms.setOnClickListener {
            findNavController().navigate(R.id.action_nav_home_patient_to_nav_intake_forms)
        }

        return view
    }

    private fun loadPatientNotifications() {
        Log.d(TAG, "Fetching patient notifications")
        // Get the token from SharedPreferences or another secure location
        val sharedPref = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val tokenResponse = sharedPref.getString("bearerToken", null) // Updated key name

        tokenResponse?.let {
            try {
                val jsonObject = JSONObject(it)
                val token = jsonObject.getString("token") // Extract the token

                apiService.getPatientNotifications("Bearer $token").enqueue(object : Callback<NotificationsResponse> {
                    override fun onResponse(call: Call<NotificationsResponse>, response: Response<NotificationsResponse>) {
                        if (response.isSuccessful) {
                            response.body()?.let { notificationsResponse ->
                                updateNotifications(notificationsResponse.notifications) // Use updateNotifications to refresh the list
                            } ?: run {
                                Log.e(TAG, "No notifications found in response")
                            }
                        } else {
                            Log.e(TAG, "Failed to fetch notifications: ${response.message()}")
                        }
                    }

                    override fun onFailure(call: Call<NotificationsResponse>, t: Throwable) {
                        Log.e(TAG, "Error fetching notifications", t)
                    }
                })
            } catch (e: JSONException) {
                Log.e(TAG, "Error parsing token: ${e.message}")
            }
        } ?: Log.d(TAG, "Token is null, user not logged in.")
    }

    private fun updateNotifications(newNotifications: List<Notification>) {
        notificationList.clear()
        notificationList.addAll(newNotifications)
        updateNotificationCount(newNotifications.size) // Update count if needed
    }

    private fun updateNotificationCount(count: Int) {
        Log.d(TAG, "Updating notification count: $count")
        txtNotificationCount.text = count.toString()
        txtNotificationCount.visibility = if (count > 0) View.VISIBLE else View.GONE

        // Update SharedPreferences if needed
        val sharedPref = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putInt("notificationCount", count)
            apply()
        }
    }
}





