package com.example.xbcad7319_physiotherapyapp.ui.forget_password_staff

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.xbcad7319_physiotherapyapp.R

class ForgetPasswordStaffFragment : Fragment() {

    companion object {
        fun newInstance() = ForgetPasswordStaffFragment()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_forget_password_staff, container, false)

        // add code logic here
    }
}