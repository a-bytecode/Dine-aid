package com.example.dine_aid.remote

import com.example.dine_aid.BuildConfig
import com.example.dine_aid.data.RecipeResponse
import com.example.dine_aid.data.RecipeResult
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


const val BASE_URL = "https://api.spoonacular.com/"

const val API_TOKEN = BuildConfig.API_TOKEN

private val client: OkHttpClient = OkHttpClient.Builder()
    .addInterceptor { chain ->
        val newRequest: Request = chain.request().newBuilder()
            .addHeader("x-api-key", API_TOKEN) // Use the correct header name
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
        @Query("query") query: String
    ) : RecipeResponse

    object RecipeApi {
        val retrofitService: RecipeApiService by lazy { retrofit.create(RecipeApiService::class.java) }
    }
}
