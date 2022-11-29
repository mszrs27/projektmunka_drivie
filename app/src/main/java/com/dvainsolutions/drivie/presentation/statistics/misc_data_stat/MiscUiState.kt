package com.dvainsolutions.drivie.presentation.statistics.misc_data_stat

import com.dvainsolutions.drivie.data.model.InsuranceType

data class MiscUiState(
    var vehicle: String = "",
    var price: String = "",
    var date: String = "",
    var insuranceType: String = InsuranceType.CASCO.name,
    var vehicleType: String = "",
    var regionalType: String = "",
    var endDate: String = "",
    var isLoading: Boolean = false
)
