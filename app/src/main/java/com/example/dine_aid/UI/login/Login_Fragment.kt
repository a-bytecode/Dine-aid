package com.example.dine_aid.UI.login

import android.os.Binder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.dine_aid.databinding.LoginScreenBinding
import com.example.dine_aid.model.FirebaseViewModel
import com.example.dine_aid.model.MainViewModel

class Login_Fragment : Fragment() {

    private lateinit var binding : LoginScreenBinding

    private val viewModel : MainViewModel by activityViewModels()

    private val firebaseViewModel : FirebaseViewModel by activityViewModels()

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

        when(viewModel.authType) {
            viewModel.authType -> {

                binding.loginbtng.setOnClickListener {
                    Log.d("isCLicked", "before input")
                    val emailInput = binding.editTextTextEmailAddress.text.toString()
                    val pwdInput = binding.editTextTextPassword.text.toString()
                    Log.d("isCLicked", "after input")

                    if (emailInput.isNotEmpty() && pwdInput.isNotEmpty()) {
                        firebaseViewModel.createAccount(emailInput,pwdInput)
                    } else {
                        Toast.makeText(requireContext(),"Sign In Failed",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
            viewModel.authType -> {
                // TODO Login
            }
        }

        firebaseViewModel.currentUser.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                Log.d("LoggingA","Login Accepted to $user")
            } else {
                Log.d("LoggingB","Login was not Accepted to $user")
            }
        }
    }
}