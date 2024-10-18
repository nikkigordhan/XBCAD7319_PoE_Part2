package com.example.xbcad7319_physiotherapyapp.ui.form2

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.xbcad7319_physiotherapyapp.R

class Form2Fragment : Fragment() {

    companion object {
        fun newInstance() = Form2Fragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_form2, container, false)
    }
}