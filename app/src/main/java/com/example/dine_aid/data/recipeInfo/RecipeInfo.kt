package com.example.dine_aid.data.recipeInfo

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class RecipeInfo (

    @Json(name = "id")
    val id: Int,

    @Json(name = "image")
    val image: String?,

    @Json(name = "readyInMinutes")
    val readyInMinutes: Int,

    @Json(name = "servings")
    val servings: Int,

    @Json(name = "sourceUrl")
    val sourceUrl: String,

    @Json(name = "title")
    val title: String,

    @Json(name = "instructions")
    var instructions: String
        )