package com.example.plswork.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import com.example.plswork.data.RecipeDetail

/**
 * Recipe model used for both:
 * - findByIngredients results
 * - complexSearch results (when addRecipeInformation=true)
 *
 * Student note:
 * - We keep defaults / nullable values so the app does not crash if a field is missing.
 */
data class Recipe(
    val id: Int,
    val title: String,
    val image: String = "",              // Spoonacular image URL or filename
    val readyInMinutes: Int? = null,     // Only available if addRecipeInformation=true
    val usedIngredientCount: Int = 0,    // Mainly from findByIngredients
    val missedIngredientCount: Int = 0   // Mainly from findByIngredients
)

/**
 * Response wrapper for Spoonacular "complexSearch"
 */
data class RecipeSearchResponse(
    val results: List<Recipe>,
    val offset: Int = 0,
    val number: Int = 0,
    val totalResults: Int = 0
)

interface SpoonacularApi {

    /**
     * Search by ingredients (your Pantry screen)
     */
    @GET("recipes/findByIngredients")
    suspend fun searchRecipes(
        @Query("apiKey") apiKey: String,
        @Query("ingredients") ingredients: String,
        @Query("number") number: Int = 10
    ): List<Recipe>

    /**
     * Search by query + filters (your Search screen)
     *
     * Useful filter params we added:
     * - diet: vegetarian, vegan, etc.
     * - maxReadyTime: real "quick" filter
     * - sort: e.g., healthiness, popularity, time
     * - type: e.g., soup, main course, dessert (helps "comfort" be different)
     *
     * Student note:
     * addRecipeInformation=true gives extra fields like readyInMinutes, sourceUrl etc.
     */
    @GET("recipes/complexSearch")
    suspend fun searchRecipesByQuery(
        @Query("apiKey") apiKey: String,
        @Query("query") query: String,
        @Query("diet") diet: String? = null,
        @Query("maxReadyTime") maxReadyTime: Int? = null,
        @Query("sort") sort: String? = null,
        @Query("type") type: String? = null,
        @Query("addRecipeInformation") addRecipeInformation: Boolean = true,
        @Query("number") number: Int = 10
    ): RecipeSearchResponse

    /**
     * Recipe details screen
     */
    @GET("recipes/{id}/information")
    suspend fun getRecipeDetails(
        @Path("id") recipeId: Int,
        @Query("apiKey") apiKey: String,
        @Query("includeNutrition") includeNutrition: Boolean = false
    ): RecipeDetail
    @GET("recipes/complexSearch")
    suspend fun searchRecipesByQuery(
        @Query("apiKey") apiKey: String,
        @Query("query") query: String,

        // existing ones you already use
        @Query("diet") diet: String? = null,
        @Query("maxReadyTime") maxReadyTime: Int? = null,
        @Query("sort") sort: String? = null,
        @Query("type") type: String? = null,

        // ✅ NEW: ingredient chips
        @Query("includeIngredients") includeIngredients: String? = null,

        // ✅ NEW: nutrition filters (grams / kcal)
        @Query("minProtein") minProtein: Int? = null,
        @Query("maxCalories") maxCalories: Int? = null,
        @Query("minFiber") minFiber: Int? = null,

        // ✅ NEW: allergy / intolerances (comma separated)
        @Query("intolerances") intolerances: String? = null,

        @Query("addRecipeInformation") addRecipeInformation: Boolean = true,
        @Query("number") number: Int = 10
    ): RecipeSearchResponse
}

object ApiClient {
    private const val BASE_URL = "https://api.spoonacular.com/"

    // Student note:
    // Using "lazy" so Retrofit is created once and reused.
    val api: SpoonacularApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SpoonacularApi::class.java)
    }
}
