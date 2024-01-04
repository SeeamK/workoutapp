package com.example.workoutcollector

import java.util.ArrayList

data class FilterItem (
    val icon: Int,
    val name: String
)

data class workoutItem (
    val id: String, // Changed to String because IDs in JSON are strings
    val name: String,
    val target: String,
    val equipment: String,
    val gifUrl: String,
    val instructions: List<String>,
    val secondaryMuscles: List<String>? = null,
    val bodyPart: String? = null
)