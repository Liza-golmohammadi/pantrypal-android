package com.example.plswork.data

import com.google.gson.annotations.SerializedName

data class RecipeDetail(
    val id: Int,
    val title: String,
    val image: String,
    val readyInMinutes: Int,
    val servings: Int,
    val summary: String,
    @SerializedName("sourceUrl")
    val sourceUrl: String = "",
    @SerializedName("analyzedInstructions")
    val analyzedInstructions: List<InstructionSet> = emptyList()
)

data class InstructionSet(
    val name: String?,
    val steps: List<Step> = emptyList()
)

data class Step(
    val number: Int,
    val step: String,
    val length: StepLength? = null
)

data class StepLength(
    val number: Int,
    val unit: String
)
