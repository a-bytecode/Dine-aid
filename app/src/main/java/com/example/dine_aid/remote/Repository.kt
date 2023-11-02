package com.example.dine_aid.remote

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dine_aid.BuildConfig
import com.example.dine_aid.data.RecipeResponse
import com.example.dine_aid.data.RecipeResult
import com.example.dine_aid.data.recipeInfo.RecipeInfo

class Repository (private val api : RecipeApiService.RecipeApi) {


    private val _recipes = MutableLiveData<List<RecipeResult>>()
    val recipes : LiveData<List<RecipeResult>> = _recipes

    private val _recipeID = MutableLiveData<Int>()
    val recipesID : LiveData<Int> = _recipeID

    private val _ingredientWidgetImage = MutableLiveData<String>()
    val ingredientWidgetImage: LiveData<String> = _ingredientWidgetImage



    private val _recipeInfo = MutableLiveData<RecipeInfo>()
    val recipeInfo : LiveData<RecipeInfo> = _recipeInfo

    suspend fun getRecipes(userInput: String) {

        val response : RecipeResponse = api.retrofitService.searchRecipes(userInput,"636x393")

        _recipes.value = response.recipes

    }

    fun loadRecipeNutritionByID(recipeID: Int) : String {

        val secretApiKey = BuildConfig.API_TOKEN

        val url = "https://api.spoonacular.com/recipes/$recipeID/nutritionWidget.png?apiKey=$secretApiKey"

        _ingredientWidgetImage.postValue(url)

        return url
    }

    suspend fun loadRecipeInfo(recipeID: Int) {

        val responseRecipeInfo = api.retrofitService.getRecipeInformation(recipeID)
        Log.d("responseRecipeInfo","responseRecipeInfo ${recipeID}")

        _recipeInfo.value = responseRecipeInfo
        Log.d("_recipeInfo","_recipeInfo -> ${responseRecipeInfo.title}")

    }

}