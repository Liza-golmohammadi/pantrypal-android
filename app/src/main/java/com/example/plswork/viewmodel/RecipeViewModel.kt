package com.example.plswork.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plswork.Constants
import com.example.plswork.data.RecipeDetail
import com.example.plswork.network.ApiClient
import com.example.plswork.network.Recipe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class RecipeUiState {
    object Idle : RecipeUiState()
    object Loading : RecipeUiState()
    data class Success(val recipes: List<Recipe>) : RecipeUiState()
    data class Error(val message: String) : RecipeUiState()
}

sealed class RecipeDetailUiState {
    object Idle : RecipeDetailUiState()
    object Loading : RecipeDetailUiState()
    data class Success(val recipeDetail: RecipeDetail) : RecipeDetailUiState()
    data class Error(val message: String) : RecipeDetailUiState()
}

class RecipeViewModel : ViewModel() {

    private val _recipeState = MutableStateFlow<RecipeUiState>(RecipeUiState.Idle)
    val recipeState: StateFlow<RecipeUiState> = _recipeState.asStateFlow()

    private val _recipeDetailState = MutableStateFlow<RecipeDetailUiState>(RecipeDetailUiState.Idle)
    val recipeDetailState: StateFlow<RecipeDetailUiState> = _recipeDetailState.asStateFlow()

    // Favorites list
    private val _favorites = MutableStateFlow<Set<Int>>(emptySet())
    val favorites: StateFlow<Set<Int>> = _favorites.asStateFlow()

    fun searchRecipes(ingredientNames: List<String>) {
        if (ingredientNames.isEmpty()) {
            _recipeState.value = RecipeUiState.Error("Please select at least one ingredient")
            return
        }

        viewModelScope.launch {
            _recipeState.value = RecipeUiState.Loading
            try {
                val ingredientsList = ingredientNames.joinToString(",") { it.lowercase() }
                val result = ApiClient.api.searchRecipes(
                    apiKey = Constants.SPOONACULAR_API_KEY,
                    ingredients = ingredientsList,
                    number = Constants.DEFAULT_RECIPE_COUNT
                )
                _recipeState.value = RecipeUiState.Success(result)
            } catch (e: Exception) {
                _recipeState.value = RecipeUiState.Error(
                    "Failed to fetch recipes: ${e.localizedMessage ?: "Unknown error"}"
                )
            }
        }
    }

    fun searchByQuery(query: String, diet: String? = null) {
        viewModelScope.launch {
            _recipeState.value = RecipeUiState.Loading
            try {
                val result = ApiClient.api.searchRecipesByQuery(
                    apiKey = Constants.SPOONACULAR_API_KEY,
                    query = query,
                    diet = diet,
                    number = Constants.DEFAULT_RECIPE_COUNT
                )
                _recipeState.value = RecipeUiState.Success(result.results)
            } catch (e: Exception) {
                _recipeState.value = RecipeUiState.Error(
                    "Failed to fetch recipes: ${e.localizedMessage ?: "Unknown error"}"
                )
            }
        }
    }

    fun getRecipeDetails(recipeId: Int) {
        viewModelScope.launch {
            _recipeDetailState.value = RecipeDetailUiState.Loading
            try {
                val details = ApiClient.api.getRecipeDetails(
                    recipeId = recipeId,
                    apiKey = Constants.SPOONACULAR_API_KEY,
                    includeNutrition = false
                )
                _recipeDetailState.value = RecipeDetailUiState.Success(details)
            } catch (e: Exception) {
                _recipeDetailState.value = RecipeDetailUiState.Error(
                    "Failed to load recipe details: ${e.localizedMessage ?: "Unknown error"}"
                )
            }
        }
    }

    fun toggleFavorite(recipeId: Int) {
        val current = _favorites.value.toMutableSet()
        if (current.contains(recipeId)) {
            current.remove(recipeId)
        } else {
            current.add(recipeId)
        }
        _favorites.value = current
    }

    fun isFavorite(recipeId: Int): Boolean {
        return _favorites.value.contains(recipeId)
    }

    fun resetRecipeState() {
        _recipeState.value = RecipeUiState.Idle
    }

    fun resetDetailState() {
        _recipeDetailState.value = RecipeDetailUiState.Idle
    }
}
