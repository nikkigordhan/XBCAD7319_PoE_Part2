package com.example.xbcad7319_physiotherapyapp.ui.forget_password_patient

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.xbcad7319_physiotherapyapp.R

class ForgetPasswordPatientFragment : Fragment() {

    companion object {
        fun newInstance() = ForgetPasswordPatientFragment()
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_forget_password_patient, container, false)


        // aad code logic here
    }
}