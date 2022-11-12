package com.dvainsolutions.drivie.presentation.refuel.details

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dvainsolutions.drivie.service.FirestoreService
import com.dvainsolutions.drivie.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RefuelDetailsViewModel @Inject constructor(
    private val firestoreService: FirestoreService,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    var refuelDetailsState by mutableStateOf(RefuelDetailsState())

    var selectedCarId: String = ""
        private set

    fun getRefuelDetails(refuelId: String) {
        getSavedCarId()
        refuelDetailsState = refuelDetailsState.copy(isLoading = true)
        firestoreService.getRefuelDetails(
            carId = selectedCarId,
            refuelId = refuelId,
            onResult = {
                refuelDetailsState = refuelDetailsState.copy(data = it, isLoading = false)
            },
            onError = {
                refuelDetailsState = refuelDetailsState.copy(isLoading = false)
            }
        )
    }

    private fun getSavedCarId() {
        viewModelScope.launch {
            val preferences = dataStore.data.first()
            preferences[stringPreferencesKey(Constants.SELECTED_CAR_ID)]?.let {
                selectedCarId = it
            }
        }
    }
}