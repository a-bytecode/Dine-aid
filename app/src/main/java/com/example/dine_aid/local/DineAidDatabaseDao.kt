package com.example.dine_aid.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.dine_aid.data.RecipeResult

@Dao
interface DineAidDatabaseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipeResults(recipeResults: List<RecipeResult>)

    @Query("SELECT * FROM RecipeResult")
    suspend fun getAllRecipes(): List<RecipeResult>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: RecipeResult)

}