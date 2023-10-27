package com.example.dine_aid.model

import android.content.Context
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dine_aid.R
import com.example.dine_aid.data.ModalBottomSheet
import com.example.dine_aid.remote.RecipeApiService
import com.example.dine_aid.remote.Repository
import com.google.android.gms.dynamic.SupportFragmentWrapper
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

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

    fun loadRecipeNutrionByID(recipeID : Int) {
        try {
            viewModelScope.launch {
                repo.loadRecipeNutrionByID(recipeID)
            }
        } catch (e:Exception) {
            Log.d("Request RecipeID", "No Response by this ID -> $recipeID / $e")

        }
    }


    fun useBottomSheet(supportFragmentManager:FragmentManager) {

        val modelBottomSheet = ModalBottomSheet()

        modelBottomSheet.show(supportFragmentManager,ModalBottomSheet.TAG)
    }

    fun slideInFromLeftAnimationTV(animatedTextView: TextView, context: Context) {

        val animationSlideFromLeft = AnimationUtils.loadAnimation(
            context,
            R.anim.slide_in_from_left
        )

        animatedTextView.startAnimation(animationSlideFromLeft)
    }



}