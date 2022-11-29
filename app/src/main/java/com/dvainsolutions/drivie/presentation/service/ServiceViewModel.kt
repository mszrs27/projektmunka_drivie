package com.dvainsolutions.drivie.presentation.service

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
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
import com.dvainsolutions.drivie.common.snackbar.SnackbarMessage
import com.dvainsolutions.drivie.data.model.Service
import com.dvainsolutions.drivie.data.model.VehiclePart
import com.dvainsolutions.drivie.data.model.VehiclePartList
import com.dvainsolutions.drivie.service.FirestoreService
import com.dvainsolutions.drivie.utils.Constants
import com.dvainsolutions.drivie.utils.UiText
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ServiceViewModel @Inject constructor(
    private val firestoreService: FirestoreService,
    private val dataStore: DataStore<Preferences>,
    private val application: Application
) : ViewModel() {

    var uiState = mutableStateOf(ServiceUiState())

    var isLoading by mutableStateOf(false)
        private set

    var vehicleList = mapOf<String, String>()
        private set

    private var partName by mutableStateOf(VehiclePartList.parts.first().name)
    var partCost by mutableStateOf("")
        private set
    val replacedParts = mutableStateMapOf<String, Int>()

    var jobName by mutableStateOf("")
        private set
    var jobCost by mutableStateOf("")
        private set
    val jobsDone = mutableStateMapOf<String, Int>()

    private val carName get() = uiState.value.vehicle
    private val date get() = uiState.value.date
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

    fun onMileageChange(value: String) {
        uiState.value = uiState.value.copy(mileage = value)
    }

    fun setPartNameText(text: String) {
        partName = text
    }

    fun setPartCostText(text: String) {
        partCost = text
    }

    fun setJobNameText(text: String) {
        jobName = text
    }

    fun setJobCostText(text: String) {
        jobCost = text
    }

    fun addItemsToPartsList(onResult: () -> Unit) {
        if (partName.isEmpty() || partName.isBlank()) {
            SnackbarManager.showMessage(
                SnackbarMessage.StringSnackbar(
                    UiText.StringResource(resId = R.string.error_choose_part)
                        .asString(application)
                )
            )
        } else if (!replacedParts.containsKey(partName)) {
            if (partCost.isEmpty()) partCost = "0"
            replacedParts.put(partName, partCost.toInt())
            partCost = ""
            onResult.invoke()
        } else {
            SnackbarManager.showMessage(
                SnackbarMessage.StringSnackbar(
                    UiText.StringResource(resId = R.string.error_duplicate_part_in_list)
                        .asString(application)
                )
            )
        }
    }

    fun removeItemsFromPartList(partName: String) {
        replacedParts.remove(partName)
    }

    fun addItemsToJobsDoneList(onResult: () -> Unit) {
        if (jobName.isEmpty() || jobName.isBlank()) {
            SnackbarManager.showMessage(
                SnackbarMessage.StringSnackbar(
                    UiText.StringResource(resId = R.string.error_choose_job)
                        .asString(application)
                )
            )
        } else if (!jobsDone.containsKey(jobName)) {
            if (jobCost.isEmpty()) jobCost = "0"
            jobsDone.put(jobName, jobCost.toInt())
            jobCost = ""
            onResult.invoke()
        } else {
            SnackbarManager.showMessage(
                SnackbarMessage.StringSnackbar(
                    UiText.StringResource(resId = R.string.error_duplicate_job_in_list)
                        .asString(application)
                )
            )
        }
    }

    fun removeItemsFromJobsDoneList(jobName: String) {
        jobsDone.remove(jobName)
    }

    fun saveServiceData(onNavigation: (id: String) -> Unit) {
        viewModelScope.launch {
            isLoading = true
            val service = Service(
                vehicle = carName,
                date = Timestamp(date.toDate() ?: Calendar.getInstance().time),
                replacedParts = replacedParts,
                jobsDone = jobsDone,
                mileage = mileage.toIntOrNull()
            )

            vehicleList[carName]?.let { id ->
                firestoreService.saveService(
                    service = service,
                    carId = id,
                    onResult = { task ->
                        if (task.exception == null) {
                            val vehicleParts = mutableListOf<VehiclePart>()
                            replacedParts.map { data ->
                                VehiclePartList.parts.firstOrNull { it.name == data.key }?.apply {
                                    currentHealth = maxLifeSpan
                                    replacementTime = Timestamp.now()
                                    vehicleParts.add(this)
                                }
                            }

                            firestoreService.updateParts(
                                carId = id,
                                value = vehicleParts,
                                onResult = {
                                    if (it == null) {
                                        onNavigation.invoke(task.result.id)
                                    } else {
                                        SnackbarManager.onError(task.exception!!)
                                    }
                                })
                        } else {
                            isLoading = false
                            SnackbarManager.onError(task.exception!!)
                        }
                    }
                )
            }
        }
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
                it[stringPreferencesKey(Constants.SELECTED_CAR_ID)] =
                    vehicleList[uiState.value.vehicle].toString()
            }
        }
    }
}