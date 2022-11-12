package com.dvainsolutions.drivie.presentation.trip.details

import com.dvainsolutions.drivie.data.model.Trip

data class TripDetailsState(
    val data: Trip = Trip(),
    val isLoading: Boolean = false
)
