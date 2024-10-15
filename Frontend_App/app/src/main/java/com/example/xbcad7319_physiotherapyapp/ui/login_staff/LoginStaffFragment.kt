package com.example.xbcad7319_physiotherapyapp.ui.login_staff

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.xbcad7319_physiotherapyapp.R
import com.example.xbcad7319_physiotherapyapp.databinding.FragmentLoginStaffBinding

class LoginStaffFragment : Fragment() {

    companion object {
        fun newInstance() = LoginStaffFragment()
    }

    private var _binding: FragmentLoginStaffBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginStaffBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up forgot password click listener
        binding.txtForgotPassword.setOnClickListener {
            onForgotPasswordClicked(it)
        }

        // add code logic here
    }

    private fun onForgotPasswordClicked(view: View) {
        findNavController().navigate(R.id.action_nav_login_staff_to_nav_forget_password_staff)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
