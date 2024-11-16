package com.example.xbcad7319_physiotherapyapp.ui.profile_patient

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.xbcad7319_physiotherapyapp.R
import com.example.xbcad7319_physiotherapyapp.ui.ApiClient
import com.example.xbcad7319_physiotherapyapp.ui.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfilePatientFragment : Fragment() {

    private lateinit var etxtEmail: EditText
    private lateinit var etxtPhoneNumber: EditText
    private lateinit var etxtMedicalAid: EditText
    private lateinit var etxtMedicalAidNumber: EditText
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button
    private lateinit var ibtnHome: ImageButton

    private lateinit var sharedPref: SharedPreferences

    private val apiService: ApiService by lazy {
        ApiClient.getRetrofitInstance(requireContext()).create(ApiService::class.java)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile_patient, container, false)

        // Initialize the ImageButton using the inflated view
        val ibtnHome: ImageButton = view.findViewById(R.id.ibtnHome)

        // Set OnClickListener for the Home button
        ibtnHome.setOnClickListener {
            findNavController().navigate(R.id.action_nav_patient_profile_to_home_patient)
        }

        initializeViews(view)

        setupListeners()

        // Initialize SharedPreferences
        sharedPref = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE)

        // Load patient profile
        loadPatientProfile()

        return view
    }

    private fun initializeViews(view: View) {
        etxtEmail = view.findViewById(R.id.etxtEmail)
        etxtPhoneNumber = view.findViewById(R.id.etxtPhone_Number)
        etxtMedicalAid = view.findViewById(R.id.etxtMedical_Aid)
        etxtMedicalAidNumber = view.findViewById(R.id.etxtMedical_Aid_Number)
        btnSave = view.findViewById(R.id.btnSave)
        btnCancel = view.findViewById(R.id.btnCancel)
        ibtnHome = view.findViewById(R.id.ibtnHome)
    }

    private fun setupListeners() {
        btnSave.setOnClickListener {
            saveProfile()
        }

        btnCancel.setOnClickListener {
            requireActivity().onBackPressed()
        }

        ibtnHome.setOnClickListener {
            findNavController().navigate(R.id.action_nav_patient_profile_to_home_patient)
        }
    }

    private fun loadPatientProfile() {
        // Get the token from SharedPreferences
        val token = sharedPref.getString("bearerToken", "") ?: ""
        if (token.isEmpty()) {
            showToast("Not authenticated. Please login again.")
            findNavController().navigate(R.id.action_nav_patient_profile_to_login_patient)
            return
        }

        // Make sure to include "Bearer " prefix
        val authToken = "Bearer $token"

        apiService.getPatientProfile(authToken).enqueue(object : Callback<Map<String, Any>> {
            override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                if (response.isSuccessful) {
                    response.body()?.let { userData ->
                        etxtEmail.setText(userData["email"]?.toString() ?: "")
                        etxtPhoneNumber.setText(userData["phoneNumber"]?.toString() ?: "")
                        etxtMedicalAid.setText(userData["medicalAid"]?.toString() ?: "")
                        etxtMedicalAidNumber.setText(userData["medicalAidNumber"]?.toString() ?: "")
                    }
                } else {
                    // Handle error response
                    if (response.code() == 401) {
                        showToast("Session expired. Please login again.")
                        findNavController().navigate(R.id.action_nav_patient_profile_to_login_patient)
                    } else {
                        showToast("Failed to load profile: ${response.message()}")
                    }
                }
            }

            override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                showToast("Error loading profile: ${t.message}")
            }
        })
    }

    private fun saveProfile() {
        val token = sharedPref.getString("bearerToken", "") ?: ""
        if (token.isEmpty()) {
            showToast("Not authenticated. Please login again.")
            findNavController().navigate(R.id.action_nav_patient_profile_to_login_patient)
            return
        }

        val authToken = "Bearer $token"
        val userId = sharedPref.getString("userId", "") ?: ""

        val profileUpdate = mapOf(
            "email" to etxtEmail.text.toString(),
            "phoneNumber" to etxtPhoneNumber.text.toString(),
            "medicalAid" to etxtMedicalAid.text.toString(),
            "medicalAidNumber" to etxtMedicalAidNumber.text.toString()
        )

        apiService.updatePatientProfile(authToken, userId, profileUpdate).enqueue(object : Callback<Map<String, Any>> {
            override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                if (response.isSuccessful) {
                    showToast("Profile updated successfully")
                    findNavController().popBackStack()
                } else {
                    if (response.code() == 401) {
                        showToast("Session expired. Please login again.")
                        findNavController().navigate(R.id.action_nav_patient_profile_to_login_patient)
                    } else {
                        showToast("Failed to update profile")
                    }
                }
            }

            override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                showToast("Error updating profile: ${t.message}")
            }
        })
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
