package com.example.dine_aid.remote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dine_aid.BuildConfig
import com.example.dine_aid.data.RecipeResponse
import com.example.dine_aid.data.RecipeResult
import com.example.dine_aid.data.recipeInfo.RecipeInfo
import com.example.dine_aid.local.DineAidDatabase

class Repository (private val api : RecipeApiService.RecipeApi, database: DineAidDatabase) {

    private val dB = database.dineAidDatabaseDao

    private val _recipes = MutableLiveData<List<RecipeResult>>()
    val recipes : LiveData<List<RecipeResult>> = _recipes

    private val _nutritionWidgetImage = MutableLiveData<String>()
    val nutritionWidgetImage: LiveData<String> = _nutritionWidgetImage

    private val _recipeInfo = MutableLiveData<RecipeInfo>()
    val recipeInfo : LiveData<RecipeInfo> = _recipeInfo

    suspend fun insertRecipeWithFormattedDate(recipe: RecipeResult) {
        val formattedDate = recipe.formatLastWatched()
        val recipeWithFormattedDate = recipe.copy(lastWatched = formattedDate)
        dB.insertRecipe(recipeWithFormattedDate)
    }

    suspend fun getRecipes(userInput: String) {

        val response : RecipeResponse = api.retrofitService.searchRecipes(userInput,"636x393")

        response.recipes.forEach { recipe ->
            insertRecipeWithFormattedDate(recipe)
        }

        dB.insertRecipeResults(response.recipes)
        _recipes.value = response.recipes

    }

    suspend fun loadRecipeInfo(recipeID: Int) {

        val responseRecipeInfo = api.retrofitService.getRecipeInformation(recipeID)

        if (responseRecipeInfo.instructions.contains("<ol><li>")) {
            val cleanedInstructions = removeHtmlTags(responseRecipeInfo.instructions)

            responseRecipeInfo.instructions = cleanedInstructions
        }

        _recipeInfo.value = responseRecipeInfo

    }

    fun loadRecipeNutritionWidgetByID(recipeID: Int) {

        val secretApiKey = BuildConfig.API_TOKEN

        val url=
            "https://api.spoonacular.com/recipes/$recipeID/nutritionWidget.png?apiKey=$secretApiKey"

        _nutritionWidgetImage.postValue(url)

    }

    fun removeHtmlTags(input: String): String {
        // Hier werden HTML Tags des TextView Requests entfernt
        val regex = Regex("<[^>]+>|&[a-zA-Z0-9]+;")
        return input.replace(regex, "")
    }

}