package com.example.plswork

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

data class Recipe(
    val id: Int,
    val title: String,
    val image: String,
    val usedIngredientCount: Int,
    val missedIngredientCount: Int
)

interface SpoonacularApi {
    @GET("recipes/findByIngredients")
    suspend fun searchRecipes(
        @Query("apiKey") apiKey: String,
        @Query("ingredients") ingredients: String,
        @Query("number") number: Int = 10
    ): List<Recipe>
}

object ApiClient {
    private const val BASE_URL = "https://api.spoonacular.com/"

    val api: SpoonacularApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SpoonacularApi::class.java)
    }
}