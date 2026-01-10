package com.example.plswork.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.plswork.data.OnboardingAnswers

/**
 * Shared ViewModel for onboarding.
 * Holds answers so they survive when user navigates between screens.
 *
 * IMPORTANT:
 * - We store answers inside mutableStateOf so Compose can recompose (update UI) when answers change.
 * - Always update answers using _answers.value = _answers.value.copy(...)
 *   (Do NOT mutate fields directly, or Compose might not refresh the screen.)
 */
class OnboardingViewModel : ViewModel() {

    // Backing state (Compose observable)
    private val _answers = mutableStateOf(OnboardingAnswers())

    // Public read-only access for screens
    val answers: OnboardingAnswers
        get() = _answers.value

    // ---------------- Q1 (single select) ----------------
    fun setGoal(value: String) {
        _answers.value = _answers.value.copy(goal = value)
    }

    // ---------------- Q2 (multi select) ----------------
    fun toggleBarrier(barrier: String) {
        val current = _answers.value.barriers.toMutableList()

        if (current.contains(barrier)) {
            current.remove(barrier)
        } else {
            current.add(barrier)
        }

        _answers.value = _answers.value.copy(barriers = current)
    }

    // ---------------- Q3 (multi select) ----------------
    fun toggleLike(like: String) {
        val current = _answers.value.likes.toMutableList()

        if (current.contains(like)) {
            current.remove(like)
        } else {
            current.add(like)
        }

        _answers.value = _answers.value.copy(likes = current)
    }

    // ---------------- Q4 (single select) ----------------
    fun setDietaryRequirement(choice: String) {
        // ✅ Correct way: update using copy so Compose recomposes
        _answers.value = _answers.value.copy(dietaryRequirement = choice)
    }

    // (If you use this for another question later)
    fun setCookingLevel(value: String) {
        _answers.value = _answers.value.copy(cookingLevel = value)
    }

    // Reset everything (useful if user restarts onboarding)
    fun clear() {
        _answers.value = OnboardingAnswers()
    }
}
