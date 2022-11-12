package com.dvainsolutions.drivie.presentation.refuel.details

import com.dvainsolutions.drivie.data.model.Refueling

data class RefuelDetailsState(
    val data: Refueling = Refueling(),
    val isLoading: Boolean = false
)
