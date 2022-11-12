package com.dvainsolutions.drivie.presentation.refuel

import com.dvainsolutions.drivie.data.model.FuelType

data class RefuelUiState(
    var vehicle: String = "",
    var date: String = "",
    var location: String = "",
    var type: String = FuelType.values().map { it.name }.first(),
    var quantity: String = "",
    var cost: String = "",
    var mileage: String = ""
)