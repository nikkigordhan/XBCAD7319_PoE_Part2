package com.example.xbcad7319_physiotherapyapp.ui.login_patient

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.xbcad7319_physiotherapyapp.R
import com.example.xbcad7319_physiotherapyapp.databinding.FragmentLoginPatientBinding


class LoginPatientFragment : Fragment() {

    companion object {
        fun newInstance() = LoginPatientFragment()
    }

    private var _binding: FragmentLoginPatientBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginPatientBinding.inflate(inflater, container, false)
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
        findNavController().navigate(R.id.action_nav_login_patient_to_nav_forget_password_patient)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
