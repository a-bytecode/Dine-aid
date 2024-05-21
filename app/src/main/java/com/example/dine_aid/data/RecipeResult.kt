package com.example.dine_aid.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

interface LastWatched {
    val lastWatched : String?
    fun formatLastWatched(): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss")
        return lastWatched?.let {
            try {
                LocalDateTime.parse(it,formatter).format(formatter)
            } catch (e:Exception) {
                LocalDateTime.now().format(formatter)
            }
        } ?: run {
            LocalDateTime.now().format(formatter)
        }
    }
}

interface LastAdded {
    val lastAdded : String?
    fun formatLastAdded(): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss")
        return lastAdded?.let {
            try {
                LocalDateTime.parse(it,formatter).format(formatter)
            } catch (e:Exception) {
                LocalDateTime.now().format(formatter)
            }
        } ?: run {
            LocalDateTime.now().format(formatter)
        }
    }
}

@Entity(tableName = "RecipeResult")
@JsonClass(generateAdapter = true)
data class RecipeResult(

    @PrimaryKey
    @Json(name = "id")
    val id: Int? = 0,

    @Json(name = "title")
    val title: String,

    @Json(name = "image")
    val image: String,

    @Json(name = "imageType")
    val imageType: String,

    val isFavorite : Boolean? = false,

    val lastAdded : String = "",

    @Json(name="lastWatched")
    override val lastWatched: String?,
    // Das Schlüsselwort Override bekommt der Compiler erst den Bezug zum Interface,
    // es ist eine syntaktische Anforderung des Kotlin Kompilers. Es ist erforderlich Override
    // explizit anzugeben damit es die Eigenschaft aus dem Interface überschreiben kann.
) : LastWatched {
    companion object {
        fun fromFirestoreData(data: Map<String, Any?>): RecipeResult {
            return RecipeResult(
                id = (data["id"] as? Long)?.toInt(),
                title = data["title"] as? String ?: "",
                image = data["image"] as? String ?: "",
                imageType = data["imageType"] as? String ?: "",
                isFavorite = data["isFavorite"] as? Boolean ?: false,
                lastAdded = data["lastAdded"] as? String ?: "",
                lastWatched = data["lastWatched"] as? String
            )
        }
    }
}