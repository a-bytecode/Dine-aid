package com.example.dine_aid.UI

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavOptions
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
    ): View {

        binding = LoginScreenBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        firebaseViewModel.currentUser.observe(viewLifecycleOwner) { user ->
            if (user == null &&
                findNavController().currentDestination?.id != R.id.login_Fragment) {
                findNavController().navigate(R.id.login_Fragment)
            }
            else if (user != null &&
                findNavController().currentDestination?.id != R.id.homeFragment) {
                findNavController().navigate(R.id.homeFragment)
            }
        }

        val failMessage = "Please enter both email and password"

        firebaseViewModel.currentUser.observe(viewLifecycleOwner) { user ->
            if (user != null) {

                Log.d("LoginFUserCheck","$user ist er eingeoggt ?")

                when (firebaseViewModel.currentUserType.value) {
                    MainViewModel.AuthType.LOGIN -> {

                         val options = NavOptions.Builder()
                            .setEnterAnim(R.anim.slide_in_left)
                            .build()

                        findNavController().navigate(R.id.homeFragment,null, options)

                    }
                    MainViewModel.AuthType.SIGN_IN -> {
                        Log.d("SignInA","SignIn was not Accepted to $user")
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

                        val emailInput = binding.editTextEmailAddress.text.toString()
                        val pwdInput = binding.editTextPassword.text.toString()

                        if (emailInput.isNotEmpty() && pwdInput.isNotEmpty()) {

                            firebaseViewModel.createAccount(
                                emailInput,
                                pwdInput,
                                requireContext(),
                                binding
                            )

                        } else {
                            Toast.makeText(requireContext(),failMessage,
                                Toast.LENGTH_SHORT
                            ).show()
                            firebaseViewModel.visibilityRegulator(binding,failMessage)
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

                        val emailInput = binding.editTextEmailAddress.text.toString()
                        val pwdInput = binding.editTextPassword.text.toString()

                        if (emailInput.isNotEmpty() && pwdInput.isNotEmpty()) {
                            firebaseViewModel.loginAccount(
                                emailInput,
                                pwdInput,
                                requireContext(),
                                binding
                            )
                        } else {
                            Toast.makeText(requireContext(),failMessage,
                                Toast.LENGTH_SHORT
                            ).show()
                            firebaseViewModel.visibilityRegulator(binding,failMessage)
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