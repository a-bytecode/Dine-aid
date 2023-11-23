package com.example.dine_aid.model

import android.content.Context
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dine_aid.R
import com.example.dine_aid.UI.ModalBottomSheet
import com.example.dine_aid.remote.RecipeApiService
import com.example.dine_aid.remote.Repository
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    val api = RecipeApiService.RecipeApi

    val repo = Repository(api)

    private var countCardView = 0

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

    fun hideKeyboard(context: Context, view: View) {
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun slideInFromLeftAnimationTV(animatedTextView: TextView, context: Context) {
        val animationSlideFromLeft = AnimationUtils.loadAnimation(
            context,
            R.anim.slide_in_from_left
        )
        animatedTextView.startAnimation(animationSlideFromLeft)
    }
}