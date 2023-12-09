package com.example.dine_aid.model

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

class FirebaseViewModel(application: Application) : AndroidViewModel(application) {

    private val firebaseAuth = FirebaseAuth.getInstance()

    val db = FirebaseDatabase.getInstance()

    val userReference = db.getReference("users")

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
            } else {
                Log.d("NOSUCCESS", "task is not succesful -> $task")
            }
        }
            .addOnFailureListener { e ->
            Log.e("FIREBASE_ERROR","Firebase auth failed",e)
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

        val userData  = hashMapOf(
            "userID" to user?.uid,
            "email" to user?.email
        )

        userReference.child(user?.uid ?:"DefaultUser").setValue(userData)
    }
}