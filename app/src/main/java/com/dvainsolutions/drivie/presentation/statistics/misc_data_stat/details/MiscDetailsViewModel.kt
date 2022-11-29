package com.dvainsolutions.drivie.presentation.statistics.misc_data_stat.details

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dvainsolutions.drivie.data.model.MiscType
import com.dvainsolutions.drivie.service.FirestoreService
import com.dvainsolutions.drivie.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MiscDetailsViewModel @Inject constructor(
    private val firestoreService: FirestoreService,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {
    var uiState by mutableStateOf(MiscDetailsUiState())

    var miscType by mutableStateOf(MiscType.INSURANCE)

    fun getMiscDetails(miscId: String) {
        uiState = uiState.copy(isLoading = true)
        viewModelScope.launch {
            val preferences = dataStore.data.first()
            preferences[stringPreferencesKey(Constants.SELECTED_CAR_ID)]?.let { carId ->
                firestoreService.getMiscDetails(
                    carId = carId,
                    miscId = miscId,
                    onResult = {
                        if (it != null) {
                            uiState = uiState.copy(isLoading = false, misc = it)

                            miscType = if (it.vignette != null) MiscType.VIGNETTE
                            else if (it.insurance != null) MiscType.INSURANCE
                            else MiscType.WEIGHT_TAX
                        }
                    },
                    onError = {
                        uiState = uiState.copy(isLoading = false)
                    }
                )
            }
        }
    }
}