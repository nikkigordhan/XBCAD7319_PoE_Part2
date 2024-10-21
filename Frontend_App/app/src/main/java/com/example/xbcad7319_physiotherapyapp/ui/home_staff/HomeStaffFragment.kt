package com.example.xbcad7319_physiotherapyapp.ui.home_staff

import android.content.Context
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
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

class HomeStaffFragment : Fragment() {

    companion object {
        fun newInstance() = HomeStaffFragment()
    }

    private lateinit var txtNotificationCount: TextView
    private val notificationList = mutableListOf<Notification>()
    private val TAG = "HomeStaffFragment"

    private val apiService: ApiService by lazy {
        ApiClient.getRetrofitInstance(requireContext()).create(ApiService::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_home_staff, container, false)

        // Initialize TextView for notification count
        txtNotificationCount = view.findViewById(R.id.txtNotificationCount)

        // Load notifications
        loadStaffNotifications()

        // Initialize buttons and set OnClickListeners
        val ibtnNotifications: ImageButton = view.findViewById(R.id.ibtnNotifications)
        val ibtnPatientNotes: ImageButton = view.findViewById(R.id.ibtnPatient_Notes)
        val ibtnViewPatientProfile: ImageButton = view.findViewById(R.id.ibtnPatient_Profile)
        val ibtnBilling: ImageButton = view.findViewById(R.id.ibtnBilling)
        val ibtnApp: ImageButton = view.findViewById(R.id.ibtnAppointments)

        ibtnNotifications.setOnClickListener {
            findNavController().navigate(R.id.action_nav_home_staff_to_nav_notifications_staff)
        }
        ibtnPatientNotes.setOnClickListener {
            findNavController().navigate(R.id.action_nav_home_staff_to_nav_app_notes)
        }
        ibtnViewPatientProfile.setOnClickListener {
            findNavController().navigate(R.id.action_nav_home_staff_to_nav_view_patient_profile)
        }
        ibtnBilling.setOnClickListener {
            findNavController().navigate(R.id.action_nav_home_staff_to_nav_billing)
        }
        ibtnApp.setOnClickListener {
            findNavController().navigate(R.id.action_nav_home_staff_to_nav_app_staff)
        }

        return view
    }

    private fun loadStaffNotifications() {
        Log.d(TAG, "Fetching staff notifications")

        // Get the token from SharedPreferences or another secure location
        val sharedPref = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val tokenResponse = sharedPref.getString("bearerToken", null)

        tokenResponse?.let {
            try {
                val jsonObject = JSONObject(it)
                val token = jsonObject.getString("token")

                apiService.getStaffNotifications("Bearer $token").enqueue(object :
                    Callback<NotificationsResponse> {
                    override fun onResponse(call: Call<NotificationsResponse>, response: Response<NotificationsResponse>) {
                        if (response.isSuccessful) {
                            response.body()?.let { notificationsResponse ->
                                updateNotifications(notificationsResponse.notifications)
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
        updateNotificationCount(newNotifications.size)
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

