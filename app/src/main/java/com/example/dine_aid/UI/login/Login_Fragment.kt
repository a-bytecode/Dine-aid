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
import androidx.navigation.fragment.findNavController
import com.example.dine_aid.R
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

        firebaseViewModel.currentUser.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                when (firebaseViewModel.currentUserType.value) {
                    MainViewModel.AuthType.LOGIN -> {
                        Log.d("LoggingA","Login Accepted to $user")
                        findNavController().navigate(R.id.homeFragment)
                    }
                    MainViewModel.AuthType.SIGN_IN -> {
                        Log.d("SignInA","SignIn was not Accepted to $user")
                        Toast.makeText(requireContext(),"Account Created",
                            Toast.LENGTH_LONG).show()
                    }
                    else -> if (firebaseViewModel.currentUserType.value == null) {
                        Log.d("observeFail","Observing current User Fail")
                    } else {
                        Log.d("LoggingB", "Login/Sign-In was not Accepted to $user")
                    }
                }
            }
        }

        viewModel.authType.observe(viewLifecycleOwner) { authType ->

            when(authType) {
                MainViewModel.AuthType.SIGN_IN -> {

                    binding.loginbtng.setOnClickListener {
                        Log.d("isCLicked", "before input")
                        val emailInput = binding.editTextTextEmailAddress.text.toString()
                        val pwdInput = binding.editTextTextPassword.text.toString()
                        Log.d("isCLicked", "after input")

                        if (emailInput.isNotEmpty() && pwdInput.isNotEmpty()) {
                            firebaseViewModel.createAccount(emailInput,pwdInput,requireContext())
                        } else {
                            Toast.makeText(requireContext(),"Sign In Failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    binding.loginHeader2.setOnClickListener {
                        viewModel.toggleAuthType()
                        viewModel.updateUI(
                            binding.loginbtng,
                            binding.loginHeader,
                            binding.loginHeader2
                        )
                    }
                }
                MainViewModel.AuthType.LOGIN -> {

                    binding.loginbtng.setOnClickListener {
                        val emailInput = binding.editTextTextEmailAddress.text.toString()
                        val pwdInput = binding.editTextTextPassword.text.toString()

                        if (emailInput.isNotEmpty() && pwdInput.isNotEmpty()) {
                            firebaseViewModel.loginAccount(emailInput,pwdInput,requireContext())
                            Log.d("LoggingC", "Login/Sign-In was not Accepted to $emailInput")
                        }
                    }
                    binding.loginHeader2.setOnClickListener {
                        viewModel.toggleAuthType()
                        viewModel.updateUI(
                            binding.loginbtng,
                            binding.loginHeader,
                            binding.loginHeader2
                        )
                    }
                }
                else -> {
                    Log.d("LoggingD", "Login/Sign-In was not Accepted")
                }
            }
        }
    }
}