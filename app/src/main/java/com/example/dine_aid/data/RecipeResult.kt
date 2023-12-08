package com.example.dine_aid.data

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.LocalDate
import java.time.format.DateTimeFormatter

interface lastWatched {
    val lastWatched : String?
    fun formatLastWatched(): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return LocalDate.parse(lastWatched, formatter).format(formatter)
    }
}

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

    @Json(name="lastWatched")
    override val lastWatched: String?,
    ) : lastWatched