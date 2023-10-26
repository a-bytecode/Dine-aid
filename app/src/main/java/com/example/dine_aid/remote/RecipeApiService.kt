package com.example.dine_aid.remote

import LoggingInterceptor
import com.example.dine_aid.data.RecipeResponse
import com.example.dine_aid.data.RecipeResult
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


const val BASE_URL = "https://api.spoonacular.com/"

const val API_TOKEN = com.example.dine_aid.BuildConfig.API_TOKEN

private val client: OkHttpClient = OkHttpClient.Builder()
    .addInterceptor(LoggingInterceptor())
    .addInterceptor { chain ->
        val newRequest: Request = chain.request().newBuilder()
            .addHeader("x-api-key",
                com.example.dine_aid.remote.API_TOKEN
            )
            .build()
        chain.proceed(newRequest)
    }
    .build()

val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()


private val retrofit = Retrofit.Builder()
    .client(client)
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface RecipeApiService {

    @GET("recipes/complexSearch")
    suspend fun searchRecipes(
        @Query("query") query: String,
        @Query("imageSize") imageSize: String
    ) : RecipeResponse

    @GET("recipes/{id}/ingredientWidget")
    suspend fun loadRecipeWidget(@Path("id") recipeId: Int) : Int

    object RecipeApi {
        val retrofitService: RecipeApiService by lazy { retrofit.create(RecipeApiService::class.java) }
    }
}
