package com.dvainsolutions.drivie.presentation.service.details

import com.dvainsolutions.drivie.data.model.Service

data class ServiceDetailsState(
    val data: Service = Service(),
    val isLoading: Boolean = false
)
