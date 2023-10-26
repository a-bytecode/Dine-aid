package com.example.dine_aid.model

import android.content.Context
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dine_aid.R
import com.example.dine_aid.data.ModalBottomSheet
import com.example.dine_aid.remote.RecipeApiService
import com.example.dine_aid.remote.Repository
import com.google.android.gms.dynamic.SupportFragmentWrapper
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    val api = RecipeApiService.RecipeApi

    val repo = Repository(api)

    fun getRecipes(userInput : String) {
        try {
            viewModelScope.launch {
                repo.getRecipes(userInput)
            }
        } catch (e: Exception) {
            Log.d("Request API", "No Response!")
        }
    }

    fun useBottomSheet(supportFragmentManager:FragmentManager) {

        val modelBottomSheet = ModalBottomSheet()

        modelBottomSheet.show(supportFragmentManager,modelBottomSheet.tag)
    }

    fun slideInFromLeftAnimationTV(textView: TextView, view: View, context: Context) {
        val textAnimate = view.findViewById<TextView>(R.id.bottomTV)

        val animationSlideFromLeft = AnimationUtils.loadAnimation(
            context,
            R.anim.slide_in_from_left
        )

        textAnimate.startAnimation(animationSlideFromLeft)

    }


}