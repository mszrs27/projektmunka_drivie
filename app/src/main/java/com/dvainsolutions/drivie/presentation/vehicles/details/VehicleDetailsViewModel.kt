package com.dvainsolutions.drivie.presentation.vehicles.details

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.dvainsolutions.drivie.R
import com.dvainsolutions.drivie.common.ext.toDate
import com.dvainsolutions.drivie.common.ext.toDateString
import com.dvainsolutions.drivie.common.ext.toFormattedString
import com.dvainsolutions.drivie.common.snackbar.SnackbarManager
import com.dvainsolutions.drivie.common.snackbar.SnackbarManager.onError
import com.dvainsolutions.drivie.data.model.EmissionStandard
import com.dvainsolutions.drivie.data.model.Vehicle
import com.dvainsolutions.drivie.data.model.convertInputToFuelType
import com.dvainsolutions.drivie.service.AccountService
import com.dvainsolutions.drivie.service.FirestoreService
import com.dvainsolutions.drivie.service.StorageService
import com.dvainsolutions.drivie.utils.Constants
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject

@HiltViewModel
class VehicleDetailsViewModel @Inject constructor(
    private val application: Application,
    private val storageService: StorageService,
    private val accountService: AccountService,
    private val firestoreService: FirestoreService,
) : ViewModel() {

    var uiState = mutableStateOf(VehicleDetailsUiState())
        private set

    private var vehicleData = Vehicle()
    private val carName get() = uiState.value.carName
    private val licence get() = uiState.value.licence
    private val brand get() = uiState.value.brand
    private val model get() = uiState.value.model
    private val engine get() = uiState.value.engine
    private val consumption get() = uiState.value.consumption
    private val mileage get() = uiState.value.mileage
    private val fuelType get() = uiState.value.fuelType
    private val fuelSize get() = uiState.value.fuelSize
    private val motDate get() = uiState.value.motDate
    private val emissionStandard get() = uiState.value.emissionStandard
    private val insurance get() = uiState.value.insurance
    private val pictureUrl get() = uiState.value.pictureUrl

    var hasDataChanged = mutableStateOf(false)
        private set
    var hasCameraImage by mutableStateOf(false)
    val cameraResultUri = mutableStateOf<Uri?>(null)
    val galleryResultUri = mutableStateOf<Uri?>(null)

    private var _isButtonLoading = mutableStateOf(false)
    val isButtonLoading: State<Boolean> = _isButtonLoading

    fun onCarNameChange(value: String) {
        hasDataChanged.value = value != vehicleData.nickname
        uiState.value = uiState.value.copy(carName = value)
    }

    fun onLicenceChange(value: String) {
        hasDataChanged.value = value != vehicleData.licence
        uiState.value = uiState.value.copy(licence = value)
    }

    fun onBrandChange(value: String) {
        hasDataChanged.value = value != vehicleData.brand
        uiState.value = uiState.value.copy(brand = value)
    }

    fun onModelChange(value: String) {
        hasDataChanged.value = value != vehicleData.model
        uiState.value = uiState.value.copy(model = value)
    }

    fun onEngineChange(value: String) {
        hasDataChanged.value = value != vehicleData.engineCapacity.toString()
        uiState.value = uiState.value.copy(engine = value)
    }

    fun onConsumptionChange(value: String) {
        hasDataChanged.value = value != vehicleData.consumption.toString()
        uiState.value = uiState.value.copy(consumption = value)
    }

    fun onMileageChange(value: String) {
        hasDataChanged.value = value != vehicleData.mileage.toString()
        uiState.value = uiState.value.copy(mileage = value)
    }

    fun onFuelTypeChange(value: String) {
        hasDataChanged.value = value != vehicleData.fuelType?.getLabel(application)
        uiState.value = uiState.value.copy(fuelType = value)
    }

    fun onFuelSizeChange(value: String) {
        hasDataChanged.value = value != vehicleData.fuelSize.toString()
        uiState.value = uiState.value.copy(fuelSize = value)
    }

    fun onMotDateChange(value: Calendar) {
        hasDataChanged.value = value.toDateString() != vehicleData.motDate.toString()
        uiState.value = uiState.value.copy(motDate = value.toDateString())
    }

    fun onEmissionStandardChange(value: String) {
        hasDataChanged.value = value != vehicleData.emissionType?.standard
        uiState.value = uiState.value.copy(emissionStandard = value)
    }

    fun onInsuranceChange(value: Calendar) {
        hasDataChanged.value = value.toDateString() != vehicleData.insuranceDate.toString()
        uiState.value = uiState.value.copy(insurance = value.toDateString())
    }

    fun getVehicleDetails(id: String) {
        uiState.value = uiState.value.copy(isLoading = true)
        firestoreService.getVehicleDetails(
            carId = id,
            onResult = {
                vehicleData = it
                uiState.value = uiState.value.copy(isLoading = false)
                uiState.value = uiState.value.copy(
                    pictureUrl = it.pictureUrl ?: "",
                    carName = it.nickname ?: "",
                    licence = it.licence,
                    brand = it.brand,
                    model = it.model,
                    engine = it.engineCapacity.toString(),
                    consumption = it.consumption.toString(),
                    mileage = it.mileage?.toString() ?: "",
                    fuelType = it.fuelType?.getLabel(application) ?: "",
                    fuelSize = it.fuelSize?.toString() ?: "",
                    motDate = it.motDate?.toDate()?.toFormattedString() ?: "",
                    emissionStandard = it.emissionType?.standard ?: "",
                    insurance = it.insuranceDate?.toDate()?.toFormattedString() ?: "",
                )
            },
            onError = {
                uiState.value = uiState.value.copy(isLoading = false)
                SnackbarManager.showMessage(R.string.error_getting_vehicle)
            })
    }

    fun updateVehicleDetails(id: String) {
        if (!hasDataChanged.value) return

        _isButtonLoading.value = true
        firestoreService.updateVehicleDetails(
            carId = id,
            vehicle = Vehicle(
                id = id,
                nickname = carName,
                licence = licence,
                brand = brand,
                model = model,
                engineCapacity = engine.toIntOrNull(),
                mileage = mileage.toInt(),
                consumption = consumption.toDoubleOrNull(),
                fuelType = convertInputToFuelType(fuelType),
                fuelSize = fuelSize.toIntOrNull(),
                emissionType = EmissionStandard.values()
                    .first { it.standard == emissionStandard },
                motDate = motDate.toDate()?.let { Timestamp(it) },
                insuranceDate = insurance.toDate()?.let { Timestamp(it) },
                pictureUrl = pictureUrl
            )
        ) {
            if (it == null) {
                val currUserId = accountService.getCurrentUser()?.uid
                (if (hasCameraImage) cameraResultUri.value else galleryResultUri.value)?.let { uri ->
                    storageService.uploadPicture(
                        file = uri,
                        currentUserId = currUserId!!,
                        path = Constants.VEHICLE_FOLDER,
                        onFailure = { err ->
                            err?.let { it -> onError(it) }
                        },
                        onSuccess = {
                            firestoreService.updateVehicle(
                                userId = currUserId,
                                vehicleId = id,
                                value = hashMapOf("pictureUrl" to it)
                            ) { error ->
                                _isButtonLoading.value = false
                                if (error == null) {
                                    getVehicleDetails(id)
                                } else {
                                    onError(error)
                                }
                            }
                        })
                }
                hasDataChanged.value = false
                if (cameraResultUri.value == null && galleryResultUri.value == null) {
                    _isButtonLoading.value = false
                    getVehicleDetails(id)
                }
            }
            else {
                hasDataChanged.value = false
                _isButtonLoading.value = false
                onError(it)
            }
        }
    }
}