package com.example.xbcad7319_physiotherapyapp.ui.login_patient
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.xbcad7319_physiotherapyapp.R
import com.example.xbcad7319_physiotherapyapp.databinding.FragmentLoginPatientBinding
import com.example.xbcad7319_physiotherapyapp.databinding.FragmentLoginStaffBinding
import com.example.xbcad7319_physiotherapyapp.ui.ApiClient
import com.example.xbcad7319_physiotherapyapp.ui.ApiService
import com.example.xbcad7319_physiotherapyapp.ui.LoginRequest
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginPatientFragment : Fragment() {

    private var _binding: FragmentLoginStaffBinding? = null
    private val binding get() = _binding!!

    private var passwordVisible: Boolean = false  // For password visibility toggle
    private lateinit var sharedPref: SharedPreferences
    private val TAG = "LoginStaffFragment"

    // Lazy initialization of ApiService
    private val apiService: ApiService by lazy {
        ApiClient.getRetrofitInstance(requireContext()).create(ApiService::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginStaffBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.iconViewPassword.setOnClickListener { togglePasswordVisibility() }
        binding.btnLogin.setOnClickListener { loginUser() }
        binding.txtForgotPassword.setOnClickListener { onForgotPasswordClicked() }
    }

    private fun loginUser() {
        val username = binding.etxtUsername.text.toString().trim()
        val password = binding.etxtPassword.text.toString().trim()

        if (username.isEmpty() || password.isEmpty()) {
            showToast("Please enter both username and password")
            return
        }

        val loginRequest = LoginRequest(username, password)
        loginUserToApi(loginRequest, username)
    }

    private fun loginUserToApi(loginRequest: LoginRequest, username: String) {
        val call = apiService.loginStaff(loginRequest)

        Log.d(TAG, "Sending login request: $loginRequest")
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    response.body()?.string()?.let { handleLoginResponse(it, username) }
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

    private fun handleLoginResponse(responseBody: String, username: String) {
        val jsonResponse = JSONObject(responseBody)
        val token = jsonResponse.getString("token")
        val role = jsonResponse.getString("role")

        if (role != "patient") {
            showToast("Login failed: You are not authorized to access this app.")
            return
        }

        sharedPref = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("bearerToken", token)
            putString("loggedInUsername", username)
            apply()
        }

        Log.d(TAG, "Login successful: Token=$token")
        showToast("Login successful!")
        clearFields()

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
        binding.etxtPassword.inputType = if (passwordVisible) {
            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        } else {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        binding.iconViewPassword.setImageResource(
            if (passwordVisible) R.drawable.visible_icon else R.drawable.visible_icon
        )
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