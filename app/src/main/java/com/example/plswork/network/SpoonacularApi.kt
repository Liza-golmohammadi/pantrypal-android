package com.example.plswork.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Path
import com.example.plswork.data.RecipeDetail

data class Recipe(
    val id: Int,
    val title: String,
    val image: String,
    val usedIngredientCount: Int = 0,
    val missedIngredientCount: Int = 0
)

data class RecipeSearchResponse(
    val results: List<Recipe>,
    val offset: Int,
    val number: Int,
    val totalResults: Int
)

interface SpoonacularApi {
    @GET("recipes/findByIngredients")
    suspend fun searchRecipes(
        @Query("apiKey") apiKey: String,
        @Query("ingredients") ingredients: String,
        @Query("number") number: Int = 10
    ): List<Recipe>

    @GET("recipes/complexSearch")
    suspend fun searchRecipesByQuery(
        @Query("apiKey") apiKey: String,
        @Query("query") query: String,
        @Query("diet") diet: String? = null,
        @Query("number") number: Int = 10
    ): RecipeSearchResponse

    @GET("recipes/{id}/information")
    suspend fun getRecipeDetails(
        @Path("id") recipeId: Int,
        @Query("apiKey") apiKey: String,
        @Query("includeNutrition") includeNutrition: Boolean = false
    ): RecipeDetail
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
