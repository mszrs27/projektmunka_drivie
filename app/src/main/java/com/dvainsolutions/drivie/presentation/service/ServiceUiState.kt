package com.dvainsolutions.drivie.presentation.service

data class ServiceUiState(
    var vehicle: String = "",
    var date: String = "",
    var replacedParts: Map<String, Int> = mapOf(),
    var jobsDone: Map<String, Int> = mapOf(),
    var mileage: String = ""
)
