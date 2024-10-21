package com.example.xbcad7319_physiotherapyapp.ui.login_patient

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.xbcad7319_physiotherapyapp.R
import com.example.xbcad7319_physiotherapyapp.databinding.FragmentLoginPatientBinding
import com.example.xbcad7319_physiotherapyapp.ui.ApiClient
import com.example.xbcad7319_physiotherapyapp.ui.ApiService
import com.example.xbcad7319_physiotherapyapp.ui.LoginRequest
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginPatientFragment : Fragment() {

    private var _binding: FragmentLoginPatientBinding? = null
    private val binding get() = _binding!!
    private var passwordVisible: Boolean = false
    private lateinit var sharedPref: SharedPreferences
    private val TAG = "LoginPatientFragment"

    // Create an instance of ApiService
    private val apiService: ApiService by lazy {
        ApiClient.getRetrofitInstance(requireContext()).create(ApiService::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginPatientBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Handle password visibility toggle
        binding.iconViewPassword.setOnClickListener {
            togglePasswordVisibility()
        }

        // Handle login button click
        binding.btnLogin.setOnClickListener {
            loginUser()
        }

        // Handle forgot password button click
        binding.txtForgotPassword.setOnClickListener {
            onForgotPasswordClicked()
        }
    }

    private fun loginUser() {
        val username = binding.etxtUsername.text.toString().trim()
        val password = binding.etxtPassword.text.toString().trim()

        // Validate inputs
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            showToast("Please enter both username and password")
            return
        }

        // Create a new LoginRequest object
        val loginRequest = LoginRequest(username = username, password = password)

        // Call API to log in
        loginUserToApi(loginRequest, username)
    }

    private fun loginUserToApi(loginRequest: LoginRequest, username: String) {
        val call = apiService.loginPatient(loginRequest)

        Log.d(TAG, "Sending login request: $loginRequest")

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    handleLoginResponse(responseBody, username)
                } else {
                    handleErrorResponse(response)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e(TAG, "Error: ${t.message}", t)
                showToast("An error occurred: ${t.message}")
            }
        })
    }

    private fun handleLoginResponse(responseBody: String?, username: String) {
        val jsonResponse = JSONObject(responseBody)
        val token = jsonResponse.getString("token")
        val role = jsonResponse.getString("role")

        // Check if the user type is "patient"
        if (role != "patient") {
            showToast("Login failed: You are not authorized to access this app.")
            return
        }

        // Store token and username in SharedPreferences
        sharedPref = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("bearerToken", token)
            putString("loggedInUsername", username)
            apply()
        }
        Log.e(TAG, "Login successful: Token=$token")
        showToast("Login successful!")
        clearFields()

        // Navigate to the Home screen
        findNavController().navigate(R.id.action_nav_login_patient_to_nav_home_patient)
    }

    private fun handleErrorResponse(response: Response<ResponseBody>) {
        val errorResponse = response.errorBody()?.string() ?: "Unknown error"
        Log.e(TAG, "Login failed: HTTP ${response.code()} - $errorResponse")
        showToast("Login failed: $errorResponse")
    }

    private fun onForgotPasswordClicked() {
        findNavController().navigate(R.id.action_nav_login_patient_to_nav_forget_password_patient)
    }

    private fun togglePasswordVisibility() {
        passwordVisible = !passwordVisible
        if (passwordVisible) {
            binding.etxtPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.iconViewPassword.setImageResource(R.drawable.visible_icon) // Use proper visible icon
        } else {
            binding.etxtPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.iconViewPassword.setImageResource(R.drawable.visible_icon) // Use proper hidden icon
        }
        binding.etxtPassword.setSelection(binding.etxtPassword.text.length)
    }

    private fun clearFields() {
        binding.etxtUsername.text.clear()
        binding.etxtPassword.text.clear()
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
