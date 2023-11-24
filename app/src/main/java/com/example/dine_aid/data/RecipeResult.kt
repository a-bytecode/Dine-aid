package com.example.dine_aid.data

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Entity(tableName = "RecipeResult")
@JsonClass(generateAdapter = true)
data class RecipeResult(

    @PrimaryKey
    @Json(name = "id")
    val id: Int,

    @Json(name = "title")
    val title: String,

    @Json(name = "image")
    val image: String,

    @Json(name = "imageType")
    val imageType: String,

    val lastWatched: String

) {
    @Ignore
    constructor() : this(0,"","","","")
    // Hier wird das LastWatched Attribut zum String Formatiert damit die Datenbank es als
    // Primitiven Datentyp aufnehmen kann.
    fun formatLastWatched(): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return LocalDate.parse(lastWatched, formatter).format(formatter)
    }
}