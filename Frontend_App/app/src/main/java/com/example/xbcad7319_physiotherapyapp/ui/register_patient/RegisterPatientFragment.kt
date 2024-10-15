package com.example.xbcad7319_physiotherapyapp.ui.register_patient

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.xbcad7319_physiotherapyapp.R

class RegisterPatientFragment : Fragment() {

    companion object {
        fun newInstance() = RegisterPatientFragment()
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_register_patient, container, false)
    }
}