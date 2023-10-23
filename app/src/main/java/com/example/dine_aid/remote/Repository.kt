package com.example.dine_aid.remote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dine_aid.data.RecipeResponse
import com.example.dine_aid.data.RecipeResult

class Repository (private val api : RecipeApiService.RecipeApi) {


    private val _recipes = MutableLiveData<List<RecipeResult>>()
    val recipes : LiveData<List<RecipeResult>> = _recipes


    suspend fun getRecipes(userInput: String) {

        val response : RecipeResponse = api.retrofitService.searchRecipes(userInput)

        _recipes.value = response.recipes

    }

}