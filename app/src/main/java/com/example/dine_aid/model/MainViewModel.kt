package com.example.dine_aid.model

import android.app.Application
import android.content.Context
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.dine_aid.R
import com.example.dine_aid.UI.ModalBottomSheet
import com.example.dine_aid.remote.RecipeApiService
import com.example.dine_aid.remote.Repository
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val api = RecipeApiService.RecipeApi

    val repo = Repository(api)

    private var countCardView = 0

    private val _authType = MutableLiveData<AuthType>()
    val authType : LiveData<AuthType>
        get() = _authType

    init {
        // Hier initialisiere ich authType
        // mit einem gewünschten Startwert.
        _authType.value = AuthType.LOGIN
        // der init block dient dazu um als
        // erstes bei der Ausführung der View
        // Funktionen zu starten,
        // oder wie im Beispiel Elemente zu initialisieren.
    }

    enum class AuthType {
        LOGIN, SIGN_IN
    }

    fun updateUI(button: Button, header: TextView, header2:TextView) {

        when(_authType.value) {
            AuthType.LOGIN -> {
                button.text = "Login"
                header.text = "Login"
                header2.text = "New? Sign up here"
            }
            AuthType.SIGN_IN -> {
                button.text = "Sign In"
                header.text = "Sign In"
                header2.text = "Back to login screen?"
            }
            else -> {
                Log.d("AuthTypeIsNull","Auth Type when is null")

            }
        }
    }

    fun toggleAuthType() {
        _authType.value = if (_authType.value == AuthType.LOGIN) AuthType.SIGN_IN else AuthType.LOGIN
    }

    fun getRecipes(userInput : String) {
        try {
            viewModelScope.launch {
                repo.getRecipes(userInput)
            }
        } catch (e: Exception) {
            Log.d("Request API", "No Response!")
        }
    }

    fun loadRecipeInfo(recipeID: Int) {
        try {
            viewModelScope.launch {
                repo.loadRecipeInfo(recipeID)
                Log.d("vMRecipeID","vMRecipeID -> ${recipeID}")

            }
        } catch (e:Exception) {
            Log.d("Request Recipe Info",
                "No Response by this ID -> ${recipeID}")
        }
    }

    fun useBottomSheet(supportFragmentManager:FragmentManager) {

        val modelBottomSheet = ModalBottomSheet()

        modelBottomSheet.show(supportFragmentManager, ModalBottomSheet.TAG)
    }

    fun nutrionCountCardViewOpener(holder: BottomSheetAdapter.ItemViewHolder, context: Context) {

        countCardView ++

        holder.ingriedientsWidgetIV.visibility = View.VISIBLE
        holder.ingriedientsWidgetIV.animation = AnimationUtils.
        loadAnimation(
            context,R.anim.slide_up_animation
        )
        if (countCardView == 2) {
            holder.ingriedientsWidgetIV.visibility = View.GONE
            holder.ingriedientsWidgetIV.animation = AnimationUtils.
            loadAnimation(
                context,R.anim.slide_down_animation
            )
            countCardView = 0
        }
    }

    fun infoCountCardViewOpener(holder: BottomSheetAdapter.ItemViewHolder, context: Context) {

        countCardView ++

        holder.recipeInfoCardView2.visibility = View.VISIBLE
        holder.ingriedientsWidgetIV.animation = AnimationUtils.
        loadAnimation(
            context,R.anim.slide_up_animation
        )
        if (countCardView == 2) {
            holder.recipeInfoCardView2.visibility = View.GONE
            holder.ingriedientsWidgetIV.animation = AnimationUtils.
            loadAnimation(
                context,R.anim.slide_down_animation
            )
            countCardView = 0
        }
    }

    fun closeBottomSheet(supportFragmentManager:FragmentManager) {
        val fragment = supportFragmentManager.findFragmentByTag(ModalBottomSheet.TAG)
        if (fragment is ModalBottomSheet) {
            fragment.dismiss()
        }
    }

    fun slideInFromLeftAnimationTV(animatedTextView: TextView, context: Context) {
        val animationSlideFromLeft = AnimationUtils.loadAnimation(
            context,
            R.anim.slide_in_from_left
        )
        animatedTextView.startAnimation(animationSlideFromLeft)
    }

}