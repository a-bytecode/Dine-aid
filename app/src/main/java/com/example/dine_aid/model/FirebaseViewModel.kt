package com.example.dine_aid.model

import android.app.Application
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dine_aid.databinding.LoginScreenBinding
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FirebaseViewModel(application: Application) : AndroidViewModel(application) {

    private val firebaseAuth = FirebaseAuth.getInstance()

    private val _currentUser = MutableLiveData<FirebaseUser?>(firebaseAuth.currentUser)
    val currentUser: LiveData<FirebaseUser?>
        get() = _currentUser

    private val _currentUserType = MutableLiveData<MainViewModel.AuthType>()
    val currentUserType : LiveData<MainViewModel.AuthType>
        get() = _currentUserType

    private suspend fun signInWithEmailAndPasswordAsync(email: String, password: String): AuthResult {
        return suspendCoroutine { continuation ->
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(task.result!!)
                    } else {
                        continuation.resumeWithException(task.exception!!)
                    }
                }
        }
    }

    fun createAccountIfEmailNotExists(email: String, password: String,context: Context, binding: LoginScreenBinding) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                withContext(Dispatchers.IO) {
                    try {
                        signInWithEmailAndPasswordAsync(email, password)
                    Log.d("taskEnabled", "E-Mail existiert bereits")

                    binding.existTVCardView.alpha = 1.0f
                    binding.existTV.alpha = 1.0f

                        delay(4000)

                    binding.existTVCardView.alpha = 0.0f
                    binding.existTV.alpha = 0.0f

                        Log.d("taskEnabled", "Task wird nicht ausgeführt1")
                } catch (e : FirebaseAuthException) {
                    Log.d("taskEnabled", "E-Mail existiert nicht, Account wird erstellt")
                    createAccount(email, password, context)
                }
            }
        } catch (e: Exception) {
                Log.e("CoroutineException", "Error in Coroutine: ${e.message}")
            }
        }
    }

    private fun createAccount(email: String, password: String, context: Context) {
        firebaseAuth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _currentUserType.value = MainViewModel.AuthType.SIGN_IN
                _currentUser.value = firebaseAuth.currentUser
                saveUserToDatabase(_currentUser.value)
                Toast.makeText(context,"Account created $email",Toast.LENGTH_SHORT).show()
                Log.d("SAVE_TO_DATABASE", "saveUserToDatabase executed successfully -> ${task.result}")
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

        val db = FirebaseFirestore.getInstance()

        if (user != null) {
            val userData  = hashMapOf(
                "userID" to user.uid,
                "email" to user.email
            )

            val usersCollection = db.collection("Users")

            val userDocumentReference = usersCollection.document(user.uid)

            userDocumentReference.set(userData)
                .addOnSuccessListener {
                    createUserSubCollection(userDocumentReference)
                    Log.d("SAVE_TO_DATABASE", "saveUserToDatabase executed successfully -> ${user.uid}")
                }
                .addOnFailureListener { e ->
                    Log.e("SAVE_TO_DATABASE", "Error saving user to database", e)
                }
        } else {
            Log.e("SAVE_TO_DATABASE", "User is null.")
        }
    }

    private fun createUserSubCollection(userDocumentReference: DocumentReference) {

        val watchHistoryCollection = userDocumentReference.collection("watchHistory")

        val watchHistoryData = hashMapOf(
            "id" to null,
            "title" to null,
            "image" to null,
            "lastWatched" to null
        )

        watchHistoryCollection.add(watchHistoryData)
            .addOnCompleteListener {
                Log.d("SAVE_TO_DATABASE", "Watch history created successfully")
            }
            .addOnFailureListener { e ->
                Log.e("SAVE_TO_DATABASE", "Error creating watch history", e)
            }

        val favoritesCollection = userDocumentReference.collection("Favorites")

        val favoritesData = hashMapOf(
            "id" to null,
            "title" to null,
            "image" to null,
            "lastWatched" to null
        )

        favoritesCollection.add(favoritesData)
            .addOnSuccessListener {
                Log.d("SAVE_TO_DATABASE", "Favorites created successfully")
            }
            .addOnFailureListener {e ->
                Log.e("SAVE_TO_DATABASE", "Error creating favorites", e)
            }
    }
}