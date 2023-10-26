package com.example.dine_aid.model

import android.util.Log
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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


}