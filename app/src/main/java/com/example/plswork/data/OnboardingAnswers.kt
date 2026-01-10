package com.example.plswork.data

/**
 * Stores onboarding answers.
 * Q2 and Q3 are multi-select, so they are Lists.
 */
data class OnboardingAnswers(
    val goal: String? = null,                  // Q1 (single select)
    val barriers: List<String> = emptyList(),   // Q2 (multi select)
    val likes: List<String> = emptyList(),      // Q3 (multi select)
    var dietaryRequirement: String? = null,               // Q4 later
    val cookingLevel: String? = null           // Q4 later (if you use it)
)
