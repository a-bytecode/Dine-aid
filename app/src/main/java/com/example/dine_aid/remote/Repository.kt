package com.example.dine_aid.remote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dine_aid.data.RecipeResponse
import com.example.dine_aid.data.RecipeResult
import retrofit2.Response

class Repository (private val api : RecipeApiService.RecipeApi) {


    private val _recipes = MutableLiveData<List<RecipeResult>>()
    val recipes : LiveData<List<RecipeResult>> = _recipes

    private val _recipeID = MutableLiveData<Int>()
    val recipesID : LiveData<Int> = _recipeID


    suspend fun getRecipes(userInput: String) {

        val response : RecipeResponse = api.retrofitService.searchRecipes(userInput,"636x393")

        _recipes.value = response.recipes

    }

    suspend fun loadRecipeWidget(recipeID: Int): String {
        // Hier rufst du die API auf und erhältst den HTML-Code
        val htmlCode = api.retrofitService.loadRecipeWidget(recipeID)
        return htmlCode.toString() // Gib den HTML-Code zurück
    }

}