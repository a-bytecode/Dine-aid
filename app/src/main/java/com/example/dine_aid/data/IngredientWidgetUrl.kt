package com.example.dine_aid.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class IngredientWidgetUrl(

    @Json(name = "ingredientWidgetUrl") // Neues Feld für Zutatenbild-URL
    val ingredientWidgetUrl: String

)