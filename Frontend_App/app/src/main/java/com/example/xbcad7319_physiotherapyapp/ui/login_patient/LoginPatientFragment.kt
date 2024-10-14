package com.example.xbcad7319_physiotherapyapp.ui.login_patient

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.xbcad7319_physiotherapyapp.R

class LoginPatientFragment : Fragment() {

    companion object {
        fun newInstance() = LoginPatientFragment()
    }

    private val viewModel: LoginPatientViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_login_patient, container, false)


        }
    }
