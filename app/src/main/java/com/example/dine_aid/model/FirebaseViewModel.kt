package com.example.dine_aid.model

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dine_aid.databinding.LoginScreenBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.SignInMethodQueryResult
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FirebaseViewModel(application: Application) : AndroidViewModel(application) {

    private val firebaseAuth = FirebaseAuth.getInstance()

    private val _currentUser = MutableLiveData<FirebaseUser?>(firebaseAuth.currentUser)
    val currentUser: LiveData<FirebaseUser?>
        get() = _currentUser

    private val _currentUserType = MutableLiveData<MainViewModel.AuthType>()
    val currentUserType : LiveData<MainViewModel.AuthType>
        get() = _currentUserType

    fun createAccount(email: String,password: String,context: Context) {
        firebaseAuth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _currentUserType.value = MainViewModel.AuthType.SIGN_IN
                _currentUser.value = firebaseAuth.currentUser
                saveUserToDatabase(_currentUser.value)
                Toast.makeText(context,
                    "Account Created",
                    Toast.LENGTH_SHORT)
                    .show()
                Log.d("SAVE_TO_DATABASE", "saveUserToDatabase executed successfully -> ${task.result}")
            } else {
                Log.d("NOSUCCESS", "task is not succesful -> $task")
            }
        }
            .addOnFailureListener { e ->
            Log.e("FIREBASE_ERROR","Firebase auth failed",e)
            }
    }

    fun ckeckIfEmailExist(email: String, binding: LoginScreenBinding) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val task = withContext(Dispatchers.IO) {
                    suspendCoroutine<Task<SignInMethodQueryResult>> { continuation ->
                        firebaseAuth.fetchSignInMethodsForEmail(email)
                            .addOnCompleteListener { task ->
                                continuation.resume(task)
                            }
                    }
                }
                if (task.isSuccessful) {
                    val signInMethods = task.result?.signInMethods
                    if (signInMethods != null && signInMethods.isNotEmpty()) {
                        Log.d("taskEnabled", "Task wird ausgeführt")
                    } else {
                        Log.d("taskEnabled", "Task wird nicht ausgeführt")
                        binding.existTV.alpha = 1.0f
                        delay(4000)
                        binding.existTV.alpha = 0.0f
                        Log.d("taskEnabled", "Task wird nicht ausgeführt1")
                    }
                } else {
                    Log.d("taskEnabled", "Task ist nicht erfolgreich: ${task.exception?.message}")
                }
            } catch (e:Exception) {
                Log.e("CoroutineException", "Error in Coroutine: ${e.message}")
            }

        }
    }

    fun loginAccount(email: String,password: String,context: Context) {
        firebaseAuth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _currentUserType.value = MainViewModel.AuthType.LOGIN
                    _currentUser.value = firebaseAuth.currentUser

                    Log.d("LOGIN_SUCCESS", "Login success", task.exception)
                } else {
                    Log.d("LOGIN_FAILURE", "Login failed", task.exception)
                    Toast.makeText(context,
                        "Login Failed",
                        Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

    private fun saveUserToDatabase(user: FirebaseUser?) {

        val db = FirebaseFirestore.getInstance()

        if (user != null) {

            val userData  = hashMapOf("userID" to user.uid, "email" to user.email)

            val userReference = db.collection("Users").document(user.uid)

            userReference.set(userData)
                .addOnSuccessListener {
                    Log.d("SAVE_TO_DATABASE", "saveUserToDatabase executed successfully -> ${user.uid}")
                }
                .addOnFailureListener { e ->
                    Log.e("SAVE_TO_DATABASE", "Error saving user to database", e)
                }
        } else {
            Log.e("SAVE_TO_DATABASE", "User is null.")
        }
    }
}