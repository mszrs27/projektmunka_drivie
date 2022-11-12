package com.dvainsolutions.drivie.presentation.trip

import android.app.Application
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import androidx.compose.runtime.State
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
import com.dvainsolutions.drivie.common.ext.toDate
import com.dvainsolutions.drivie.common.ext.toDateString
import com.dvainsolutions.drivie.common.snackbar.SnackbarManager
import com.dvainsolutions.drivie.data.model.Trip
import com.dvainsolutions.drivie.service.FirestoreService
import com.dvainsolutions.drivie.utils.Constants.SELECTED_CAR_ID
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


@HiltViewModel
class TripViewModel @Inject constructor(
    private val application: Application,
    private val firestoreService: FirestoreService,
    private val dataStore: DataStore<Preferences>,
    private val context: Application
) : ViewModel() {

    var uiState = mutableStateOf(TripUiState())
        private set

    private var _isButtonLoading = mutableStateOf(false)
    val isButtonLoading: State<Boolean> = _isButtonLoading

    var locations = mutableListOf<Address>()
        private set
    var vehicleList = mapOf<String, String>()
        private set

    var isStartLoc by mutableStateOf(false)
        private set
    var isTargetLoc by mutableStateOf(false)
        private set
    private var startLoc = Location("")
    private var endLoc = Location("")

    private val carName get() = uiState.value.vehicle
    private val date get() = uiState.value.date
    private val startLocation get() = uiState.value.startLocation
    private val endLocation get() = uiState.value.targetLocation
    private val mileage get() = uiState.value.mileage

    init {
        getVehicleList()
    }

    fun onVehicleChange(value: String) {
        uiState.value = uiState.value.copy(vehicle = value)
        saveCarIdToDataStore()
    }

    fun onDateChange(value: Calendar) {
        uiState.value = uiState.value.copy(date = value.toDateString())
    }

    fun onLocationChange(value: String, isStartLocation: Boolean) {
        if (isStartLocation) {
            isStartLoc = true
            isTargetLoc = false
            uiState.value = uiState.value.copy(startLocation = value)
        } else {
            isStartLoc = false
            isTargetLoc = true
            uiState.value = uiState.value.copy(targetLocation = value)
        }

        if (value.length > 5) {
            viewModelScope.launch {
                getAddress(isStartLocation)
            }
        }
        if (value.isEmpty()) {
            locations.clear()
        }
    }

    private fun getAddress(isStartLocation: Boolean) {
        val geocoder = Geocoder(context, Locale("hu"))
        if (Build.VERSION.SDK_INT >= 33) {
            val geocodeListener = Geocoder.GeocodeListener { addresses ->
                locations = addresses
            }
            geocoder.getFromLocationName(
                if (isStartLocation) uiState.value.startLocation else uiState.value.targetLocation,
                5,
                0.0,
                0.0,
                0.0,
                0.0,
                geocodeListener
            )
        } else {
            locations = geocoder.getFromLocationName(
                if (isStartLocation) uiState.value.startLocation else uiState.value.targetLocation,
                5
            ) as MutableList<Address>
        }
    }

    fun selectAddress(address: String, isStartLocation: Boolean) {
        locations.clear()

        if (isStartLocation) {
            uiState.value = uiState.value.copy(startLocation = "").copy(startLocation = address)
        } else {
            uiState.value = uiState.value.copy(targetLocation = "").copy(targetLocation = address)
        }

    }

    fun createLocationsWithLatLong(isStartLocation: Boolean, address: Address) {
        if (locations.isNotEmpty()) {
            if (isStartLocation) {
                startLoc.apply {
                    latitude = address.latitude
                    longitude = address.longitude
                }
            } else {
                endLoc.apply {
                    latitude = address.latitude
                    longitude = address.longitude
                }
            }
        }
    }

    fun onMileageChange(value: String) {
        uiState.value = uiState.value.copy(mileage = value)
    }

    private fun getVehicleList() {
        firestoreService.getVehicleNameListWithId(onResult = {
            vehicleList = it
            uiState.value = uiState.value.copy(vehicle = it.keys.first())
            saveCarIdToDataStore()
        }, onError = {
            SnackbarManager.showMessage(R.string.error_getting_vehicles)
        })
    }

    private fun saveCarIdToDataStore() {
        viewModelScope.launch {
            dataStore.edit {
                it[stringPreferencesKey(SELECTED_CAR_ID)] =
                    vehicleList[uiState.value.vehicle].toString()
            }
        }
    }

    private fun getDistanceBetweenLocations(): Float {
        return (startLoc.distanceTo(endLoc) / 1000f)
    }

    private fun calculateConsumption(consumption: Double?): Double {
        return if (consumption != null)
            ((getDistanceBetweenLocations() * consumption) / 100)
        else 0.0
    }

    fun saveTripData(onNavigation: (id: String) -> Unit) {
        if (getDistanceBetweenLocations() == 0f) {
            SnackbarManager.showMessage(R.string.error_same_locations)
            return
        }

        viewModelScope.launch {
            _isButtonLoading.value = true

            vehicleList[carName]?.let { id ->
                firestoreService.getVehicleDetails(
                    carId = id,
                    onResult = {
                        val trip = Trip(
                            carName,
                            date = Timestamp(date.toDate() ?: Calendar.getInstance().time),
                            startLocation = startLocation,
                            targetLocation = endLocation,
                            mileage = mileage,
                            distance = getDistanceBetweenLocations(),
                            consumption = calculateConsumption(it.consumption)
                        )

                        firestoreService.saveTrip(trip = trip, carId = id) { task ->
                            if (task.exception == null) {
                                onNavigation.invoke(task.result.id)
                            } else {
                                _isButtonLoading.value = false
                                SnackbarManager.onError(task.exception!!)
                            }
                        }
                    },
                    onError = {
                        _isButtonLoading.value = false
                        SnackbarManager.showMessage(R.string.error_getting_vehicle)
                    }
                )
            }
        }
    }

}