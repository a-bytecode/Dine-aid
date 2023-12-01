package com.example.dine_aid.UI.login

import android.os.Binder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.dine_aid.databinding.LoginScreenBinding
import com.example.dine_aid.model.MainViewModel

class Login_Fragment : Fragment() {

    private lateinit var binding : LoginScreenBinding

    private val viewModel : MainViewModel by activityViewModels()

    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LoginScreenBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.loginHeader2.setOnClickListener {
            viewModel.toggleAuthType()
            viewModel.updateUI(
                binding.loginbtng,
                binding.loginHeader,
                binding.loginHeader2
            )
        }
    }
}