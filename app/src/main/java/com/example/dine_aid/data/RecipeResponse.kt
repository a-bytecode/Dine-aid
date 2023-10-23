package com.example.dine_aid.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RecipeResponse(

    @Json(name = "results")
    val recipes: List<RecipeResult>,

    @Json(name = "offset")
    val offset: Int,

    @Json(name = "number")
    val number: Int,

    @Json(name = "totalResults")
    val totalResults: Int
)