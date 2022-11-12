package com.dvainsolutions.drivie.presentation.vehicles

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dvainsolutions.drivie.R
import com.dvainsolutions.drivie.common.snackbar.SnackbarManager
import com.dvainsolutions.drivie.service.FirestoreService
import com.dvainsolutions.drivie.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VehicleListViewModel @Inject constructor(
    private val firestoreService: FirestoreService,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    var vehicleListState by mutableStateOf(VehicleListState())

    init {
        getVehicleList()
    }

    private fun getVehicleList() {
        vehicleListState = vehicleListState.copy(isLoading = true)
        firestoreService.getVehicleList(
            onResult = {
                vehicleListState = vehicleListState.copy(vehicles = it, isLoading = false)
            },
            onError = {
                vehicleListState = vehicleListState.copy(isLoading = false)
                SnackbarManager.showMessage(R.string.error_getting_vehicles)
            }
        )
    }

    fun saveCarIdToDataStore(id: String) {
        viewModelScope.launch {
            dataStore.edit {
                it[stringPreferencesKey(Constants.SELECTED_CAR_ID)] = id
            }
        }
    }
}