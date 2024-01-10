package com.example.dine_aid.model

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.SetOptions
import com.example.dine_aid.data.RecipeResult
import com.example.dine_aid.databinding.LoginScreenBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
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

    private val db = FirebaseFirestore.getInstance()

    private var listenerRegistration : ListenerRegistration? = null

    private val _currentUser = MutableLiveData<FirebaseUser?>(firebaseAuth.currentUser)
    val currentUser: LiveData<FirebaseUser?>
        get() = _currentUser

    private val _currentUserType = MutableLiveData<MainViewModel.AuthType>()
    val currentUserType : LiveData<MainViewModel.AuthType>
        get() = _currentUserType

    private val _lastWatchedLiveData = MutableLiveData<List<RecipeResult>>()
    val lastWatchedLiveData : LiveData<List<RecipeResult>> = _lastWatchedLiveData


    fun fetchLastWatchedResults() {
        currentUser.value?.let { user ->
            val userDocumentReference = db.collection("Users").document(user.uid)
            val watchHistoryReference = userDocumentReference.collection("watchHistory")

            listenerRegistration = watchHistoryReference.addSnapshotListener { querySnapshot, error ->

                if (error != null) {
                    Log.e("Firestore Error", "Listen failed", error)
                    return@addSnapshotListener
                }

                val updatedList = mutableListOf<RecipeResult>()

                for (document in querySnapshot!!.documents) {
                    val recipeResult = RecipeResult.fromFirestoreData(document.data!!)

                    updatedList.add(recipeResult)

                }

                removeDuplicateWatchHistoryIds()

                val distinctList = updatedList.distinctBy { it.id }

                _lastWatchedLiveData.value = distinctList.sortedBy { it.lastWatched }

                }
            }
        }

            private fun removeDuplicateWatchHistoryIds() {
                currentUser.value?.let { user ->
                    val userDocumentReference = db.collection("Users").document(user.uid)
                    val watchHistoryReference = userDocumentReference.collection("watchHistory")

                    // recipeToWatchHistoryMap sorgt dafür,
                    // dass jede RecipeID durch die gesammte DocumentReferenz verglichen werden kann.
                    val recipeToWatchHistoryMap = mutableMapOf<Int, MutableList<String>>()


                    watchHistoryReference.get().addOnSuccessListener { querySnapshot ->

                        for (document in querySnapshot) {
                            //Durch den QuerySnapshot bekommen wir unsere RecipeID
                            val recipeResult = RecipeResult.fromFirestoreData(document.data)
                            // Default-Wert ist bei getOrPut eine Leere Liste { mutableListOf() }
                            // somit gibt es eine Leere Liste zurück wenn die RecipeID nicht existiert.
                            recipeToWatchHistoryMap.getOrPut(recipeResult.id!!) { mutableListOf() }.add(document.id)
                        }
                        for ((_, watchHistoryIds) in recipeToWatchHistoryMap) {
                            if (watchHistoryIds.size > 1) {  // Wenn es Dublikate gibt für die RecipeID,
                                // behalte ich die erste WatchHistoryID und lösche die restlichen.
                                for (i in 1 until watchHistoryIds.size) {
                                    //Hierdurch iterieren wir dann durch die Gesamte Referenz,
                                    // um das dublizierte RecipeID Element zu löschen
                                    watchHistoryReference.document(watchHistoryIds[i]).delete()
                        }
                    }
                }
            }
        }
    }

    fun updateLastWatchedForRecipe(recipeId: Int) {

        currentUser.value?.let { user ->
            val userDocumentReference = db.collection("Users").document(user.uid)
            Log.d("CheckUser", "User data: ${user.uid}")
            val watchHistoryReference = userDocumentReference.collection("watchHistory")
            Log.d("CheckWHistory", "History data: ${userDocumentReference.id}")

            watchHistoryReference.addSnapshotListener { snapShot, e ->
                if (e != null) {
                    Log.w("SnapFail", "Listen failed", e)
                    return@addSnapshotListener
                }
                if (snapShot != null) {
                    Log.d("GoodSnap", "Current data: ${snapShot.documents}")
                    val currentTimestamp = Timestamp.now()
                    watchHistoryReference.document(recipeId.toString()).update("lastWatched", currentTimestamp.toDate())
                        .addOnSuccessListener {
                            Log.d("UpdateSuccess", "Successfully update with ID: $recipeId, and ${currentTimestamp.toDate()}")
                        }
                        .addOnFailureListener { e ->
                            Log.e("UpdateFailure", "Error updating lastWatched for recipe with ID: $recipeId", e)
                        }
                } else {
                    Log.d("GoodSnap", "Current data: null")
                }
            }

//            watchHistoryReference.document(recipeId).get().addOnSuccessListener { snapShot ->
//                Log.d("AddOnFailure1", "snapShotChecker data: ${snapShot.data}")
//                if (snapShot.exists()) {
//                    watchHistoryReference.get()
//                    val currenTimestamp = Timestamp.now()
//                    Log.d("CheckTimeTime", "Time data: ${currenTimestamp}")
//                    Log.d("snapShotExist", "Snap data: ${snapShot.exists()}")
//
//
//                    watchHistoryReference.document(recipeId).update("lastWatched",currenTimestamp.toString())
//                }
//            }.addOnFailureListener {
//                Log.d("AddOnFailure2", "History data: ${userDocumentReference.id}")
//            }

        }
    }


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
                saveUserToDatabaseNoRecipeResult(_currentUser.value)
                Toast.makeText(context,
                    "Account created $email",
                    Toast.LENGTH_SHORT)
                    .show()
                Log.d("SAVE_TO_DATABASE",
                    "saveUserToDatabase executed successfully -> ${task.result}")
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

    private fun saveUserToDatabase(user: FirebaseUser?,recipeResult: RecipeResult?) {

        if (user != null) {
            val userData  = hashMapOf(
                "userID" to user.uid,
                "email" to user.email
            )

            val usersCollection = db.collection("Users")

            val userDocumentReference = usersCollection.document(user.uid)

            userDocumentReference.set(userData)
                .addOnSuccessListener {
                    recipeResult?.let {
                        createUserSubCollection(userDocumentReference,it)
                    }
                    Log.d("SAVE_TO_DATABASE",
                        "saveUserToDatabase executed successfully -> ${user.uid}")

                }
                .addOnFailureListener { e ->
                    Log.e("SAVE_TO_DATABASE", "Error saving user to database", e)
                }
        } else {
            Log.e("SAVE_TO_DATABASE", "User is null.")
        }
    }

    private fun saveUserToDatabaseNoRecipeResult(user: FirebaseUser?) {
        saveUserToDatabase(user,null)
    }


    fun saveLastWatchedResult(recipeResult: RecipeResult) {
        // Hier wird der LastwatchResult in die Collection "watchHistory gespeichert."
        if (currentUser.value != null) {
            val usersCollection = db.collection("Users")
            val userDocumentReference = usersCollection.document(currentUser.value!!.uid)

            createUserSubCollection(userDocumentReference,recipeResult)
        }
    }

    private fun createUserSubCollection(userDocumentReference: DocumentReference, recipeResult: RecipeResult) {

        val watchHistoryCollection = userDocumentReference.collection("watchHistory")

        val watchHistoryData = hashMapOf(
            "id" to recipeResult.id,
            "title" to recipeResult.title,
            "image" to recipeResult.image,
            "lastWatched" to recipeResult.lastWatched
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