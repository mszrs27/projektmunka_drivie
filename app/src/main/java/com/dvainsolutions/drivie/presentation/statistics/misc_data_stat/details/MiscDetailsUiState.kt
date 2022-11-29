package com.dvainsolutions.drivie.presentation.statistics.misc_data_stat.details

import com.dvainsolutions.drivie.data.model.MiscData

data class MiscDetailsUiState(
    val isLoading: Boolean = false,
    val misc: MiscData? = MiscData()
)
