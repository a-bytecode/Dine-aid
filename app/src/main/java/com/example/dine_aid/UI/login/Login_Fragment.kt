package com.example.dine_aid.UI.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.dine_aid.R
import com.example.dine_aid.databinding.LoginScreenBinding
import com.example.dine_aid.model.FirebaseViewModel
import com.example.dine_aid.model.MainViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class Login_Fragment : AppCompatActivity() {

    private lateinit var binding: LoginScreenBinding

    private lateinit var firebaseViewModel: FirebaseViewModel

    private lateinit var viewModel: MainViewModel

    private lateinit var userMail : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginScreenBinding.inflate(layoutInflater)
//        FirebaseApp.initializeApp(this)
        setContentView(binding.root)

        firebaseViewModel = ViewModelProvider(this)
            .get(
                FirebaseViewModel::class.java
            )

        firebaseViewModel.currentUser.observe(this) {
            if (it == null) {
                findNavController(
                    androidx.navigation.fragment.
                    R.id.nav_host_fragment_container
                ).navigate(
                        R.id.login_Fragment
                    )
            }
        }
    }
}