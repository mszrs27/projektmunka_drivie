package com.dvainsolutions.drivie.presentation.trip

data class TripUiState(
    var vehicle: String = "",
    var date: String = "",
    var startLocation: String = "",
    var targetLocation: String = "",
    var mileage: String = ""
)
