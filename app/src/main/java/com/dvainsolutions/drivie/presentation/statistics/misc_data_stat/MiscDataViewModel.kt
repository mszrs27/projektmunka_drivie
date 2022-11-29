package com.dvainsolutions.drivie.presentation.statistics.misc_data_stat

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import com.dvainsolutions.drivie.common.snackbar.SnackbarManager.onError
import com.dvainsolutions.drivie.data.model.*
import com.dvainsolutions.drivie.service.FirestoreService
import com.dvainsolutions.drivie.utils.Constants
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MiscDataViewModel @Inject constructor(
    private val firestoreService: FirestoreService,
    private val dataStore: DataStore<Preferences>,
    private val app: Application
) : ViewModel() {

    var uiState by mutableStateOf(MiscUiState())

    var vehicleList = mapOf<String, String>()
        private set

    var miscDataList = mutableStateListOf<MiscData>()
        private set

    var miscType by mutableStateOf(MiscTypeList.values().map { it.getLabel(app) }.first())

    init {
        getAllMiscData()
    }

    fun onMiscTypeChange(value: String) {
        miscType = value
        uiState = uiState.copy(
            price = "",
            date = "",
            insuranceType = "",
            vehicleType = if (value == MiscTypeList.VIGNETTE.getLabel(app)) VignetteVehicleType.values().first().name else "",
            regionalType = if (value == MiscTypeList.VIGNETTE.getLabel(app)) VignetteRegionalType.values().first().getLabel(app) else "",
            endDate = ""
        )
    }

    fun onVehicleChange(value: String) {
        uiState = uiState.copy(vehicle = value)
        saveCarIdToDataStore()
    }

    fun onDateChange(value: Calendar) {
        uiState = uiState.copy(date = value.toDateString())
    }

    fun onPriceChange(value: String) {
        uiState = uiState.copy(price = value)
    }

    fun onInsuranceTypeChange(value: String) {
        uiState = uiState.copy(insuranceType = value)
    }

    fun onVignetteVehicleTypeChange(value: String) {
        uiState = uiState.copy(vehicleType = value)
    }

    fun onEndDateChange(value: Calendar) {
        uiState = uiState.copy(endDate = value.toDateString())
    }

    private fun getAllMiscData() {
        viewModelScope.launch {
            val preferences = dataStore.data.first()
            preferences[stringPreferencesKey(Constants.SELECTED_CAR_ID)]?.let { carId ->
                uiState = uiState.copy(isLoading = true)
                firestoreService.getAllMiscData(
                    carId = carId,
                    onResult = { tripList ->
                        uiState = uiState.copy(isLoading = false)
                        tripList?.forEach {
                            miscDataList.add(it)
                        }
                    },
                    onError = { err ->
                        uiState = uiState.copy(isLoading = false)
                        err?.let { it -> onError(it)
                        }
                    }
                )
            }
        }
    }

    fun saveMiscData(onResult: () -> Unit) {
        uiState = uiState.copy(isLoading = true)

        val miscData = when (miscType) {
            MiscTypeList.INSURANCE.getLabel(app) -> {
                MiscData(
                    insurance = MiscInsurance(
                        type = uiState.insuranceType,
                        price = uiState.price.toIntOrNull(),
                        date = Timestamp(uiState.date.toDate() ?: Calendar.getInstance().time),
                    )
                )
            }
            MiscTypeList.VIGNETTE.getLabel(app) -> {
                MiscData(
                   vignette = MiscVignette(
                       vehicleType = uiState.vehicleType,
                       regionalType = uiState.regionalType,
                       price = uiState.price.toIntOrNull(),
                       startDate = Timestamp(uiState.date.toDate() ?: Calendar.getInstance().time),
                       endDate = Timestamp(uiState.endDate.toDate() ?: Calendar.getInstance().time),
                   )
                )
            }
            MiscTypeList.WEIGHT_TAX.getLabel(app) -> {
                MiscData(
                    weightTax = MiscWeightTax(
                        date = Timestamp(uiState.date.toDate() ?: Calendar.getInstance().time),
                        price = uiState.price.toIntOrNull()
                    )
                )
            }
            else -> {
                MiscData()
            }
        }

        vehicleList[uiState.vehicle]?.let { id ->
            firestoreService.saveMiscData(carId = id, miscData = miscData, onResult = {
                uiState = uiState.copy(isLoading = false)
                if (it == null) {
                    onResult.invoke()
                } else {
                    onError(it)
                }
            })
        }
    }

    fun getVehicleList() {
        firestoreService.getVehicleNameListWithId(onResult = {
            vehicleList = it
            uiState = uiState.copy(vehicle = it.keys.first())
            saveCarIdToDataStore()
        }, onError = {
            SnackbarManager.showMessage(R.string.error_getting_vehicles)
        })
    }

    private fun saveCarIdToDataStore() {
        viewModelScope.launch {
            dataStore.edit {
                it[stringPreferencesKey(Constants.SELECTED_CAR_ID)] =
                    vehicleList[uiState.vehicle].toString()
            }
        }
    }
}