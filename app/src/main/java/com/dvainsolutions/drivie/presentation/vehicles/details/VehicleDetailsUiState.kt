package com.dvainsolutions.drivie.presentation.vehicles.details

import com.dvainsolutions.drivie.data.model.EmissionStandard

data class VehicleDetailsUiState(
    val isLoading: Boolean = false,
    val pictureUrl: String = "",
    val carName: String = "",
    val licence: String = "",
    val brand: String = "",
    val model: String = "",
    val engine: String = "",
    val consumption: String = "",
    val mileage: String = "",
    val fuelType: String = "",
    val fuelSize: String = "",
    val motDate: String = "",
    val emissionStandard: String = EmissionStandard.values().map { it.standard }.first(),
    val insurance: String = ""
)
