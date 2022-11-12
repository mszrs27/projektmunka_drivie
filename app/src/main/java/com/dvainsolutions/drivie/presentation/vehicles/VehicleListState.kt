package com.dvainsolutions.drivie.presentation.vehicles

import com.dvainsolutions.drivie.data.model.Vehicle

data class VehicleListState(
    val vehicles: List<Vehicle> = emptyList(),
    val isLoading: Boolean = false
)
