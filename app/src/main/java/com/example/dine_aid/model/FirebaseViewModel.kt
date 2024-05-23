package com.example.dine_aid.model

import android.app.Application
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dine_aid.R
import com.example.dine_aid.data.RecipeResult
import com.example.dine_aid.databinding.LoginScreenBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

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

    private var accIsCreated = false

    fun deleteWatchHistory() {
        currentUser.value?.let { user ->
            val userDocumentReference = db.collection("Users").document(user.uid)
            val watchHistoryReference = userDocumentReference.collection("watchHistory")

            watchHistoryReference.get().addOnSuccessListener { documents ->
                for (document in documents) {
                    document.reference.delete()
                }
            }.addOnFailureListener { exeption ->
                Log.e("FirebaseViewModel", "Fehler beim Löschen der Daten", exeption)
            }
        }
    }

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
        // ***! WICHTIG !***
        // Der Codeabschnitt ist im Moment redundant,
        // da der SnapShotListener nicht auf die Recipe ID zugreifen kann,
        // im Firestore ist diese als Integer deklariert da die Funktion .update nur einen String nimmt.
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
        }
    }

    fun createAccount(email: String, password: String, context: Context,binding: LoginScreenBinding) {
        firebaseAuth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                firebaseAuth.currentUser?.let { user ->
                    user.sendEmailVerification()
                        .addOnCompleteListener { verifiationTask ->
                            if (verifiationTask.isSuccessful) {
                                Log.e("EmailSend","task is succesufull ${verifiationTask.isSuccessful}")
                            } else {
                                Log.e("EmailSend","task is not succesfull ${verifiationTask.isSuccessful}")
                            }
                        }
                }
                accIsCreated = true
                val accountCreatedText = "Account created $email"
                _currentUserType.value = MainViewModel.AuthType.SIGN_IN
                _currentUser.value = firebaseAuth.currentUser
                saveUserToDatabaseNoRecipeResult(_currentUser.value)
                Toast.makeText(context,
                    accountCreatedText,
                    Toast.LENGTH_SHORT)
                    .show()
                visibilityRegulator(binding, accountCreatedText)
            } else {
                accIsCreated = false
                Toast.makeText(context,
                    "${task.exception?.message}",
                    Toast.LENGTH_SHORT).show()
                task.exception?.message?.let { visibilityRegulator(binding, it) }
            }
        }
            .addOnFailureListener { e ->
            Log.e("FIREBASE_ERROR","Firebase auth failed",e)
            }
    }

    fun visibilityRegulator(binding: LoginScreenBinding,task: String) {
        if (!accIsCreated) {
            Log.d("accCreated","${accIsCreated}")
            binding.constraintExistCV.setBackgroundResource(R.drawable.existtvcolor)
            binding.existTV.setTextColor(Color.RED)
            binding.existTVCardView.visibility = View.VISIBLE
            binding.existTV.text = task
            val handler = android.os.Handler()
            handler.postDelayed({
                binding.existTVCardView.visibility = View.GONE
            }, 4000)
        } else {
            Log.d("accCreated","${accIsCreated}")
            binding.existTVCardView.visibility = View.VISIBLE
            binding.constraintExistCV.setBackgroundResource(R.drawable.existtvcolor2)
            binding.existTV.setTextColor(Color.GREEN)
            binding.existTV.text = task
            val handler = android.os.Handler()
            handler.postDelayed({
                binding.existTVCardView.visibility = View.GONE
            }, 4000)
        }
    }

    fun loginAccount(email: String,password: String,context: Context,binding: LoginScreenBinding) {
        firebaseAuth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _currentUserType.value = MainViewModel.AuthType.LOGIN
                    _currentUser.value = firebaseAuth.currentUser
                    Log.d("LOGIN_SUCCESS", "Login success", task.exception)
                } else {
                    Toast.makeText(context,
                        task.exception?.message,
                        Toast.LENGTH_SHORT)
                        .show()
                    task.exception?.message?.let { visibilityRegulator(binding, it) }
                }
            }
    }

    fun logoutAccount() {
        firebaseAuth.signOut()
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
            "isFavorite" to recipeResult.isFavorite,
            "lastAdded" to recipeResult.lastAdded,
            "lastWatched" to recipeResult.lastWatched
        )

        watchHistoryCollection.add(watchHistoryData)
            .addOnCompleteListener {
                Log.d("SAVE_TO_DATABASE", "Watch history created successfully")
            }
            .addOnFailureListener { e ->
                Log.e("SAVE_TO_DATABASE", "Error creating watch history", e)
            }
    }

    private fun getUserDocumentReference(): DocumentReference {
        val userId = firebaseAuth.currentUser?.uid ?: throw IllegalStateException("User not logged in")
        return db.collection("Users").document(userId)
    }

    fun toggleFavoriteStatus(recipeResult: RecipeResult) {

        val userDocumentReference = getUserDocumentReference()

        recipeResult.isFavorite = !recipeResult.isFavorite

        val favoritesCollection = userDocumentReference.collection("Favorites")
        val favoriteDocument = favoritesCollection.document(recipeResult.id.toString())

        if (recipeResult.isFavorite) {

            val favoritesData = hashMapOf(
                "id" to recipeResult.id,
                "title" to recipeResult.title,
                "image" to recipeResult.image,
                "isFavorite" to recipeResult.isFavorite,
                "lastAdded" to recipeResult.lastAdded,
                "lastWatched" to recipeResult.lastWatched
            )
            // * hier überschreiben wir die Favorite Attributes *
            favoritesCollection.document(recipeResult.id.toString()).set(favoritesData)
                .addOnSuccessListener {
                    Log.d("SYNC_FAVORITE", "${recipeResult.isFavorite}")
                    Log.d("SYNC_FAVORITE", "Favorites updated successfully")
                }
                .addOnFailureListener { e ->
                    Log.e("SYNC_FAVORITE", "Error updating favorites", e)
                }
        } else {
            favoriteDocument.delete()
                .addOnCompleteListener {
                    Log.d("SYNC_FAVORITE", "${recipeResult.isFavorite}")
                    Log.d("SYNC_FAVORITE", "Favorites removed successfully")
                }
                .addOnFailureListener { e ->
                    Log.e("SYNC_FAVORITE", "Error removing from favorites", e)
                }
        }
    }
}