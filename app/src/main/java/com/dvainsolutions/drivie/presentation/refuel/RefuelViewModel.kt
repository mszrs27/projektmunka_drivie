package com.dvainsolutions.drivie.presentation.refuel

import android.app.Application
import android.location.Address
import android.location.Geocoder
import android.net.Uri
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
import com.dvainsolutions.drivie.common.ext.stringWithCommaToFloatNumber
import com.dvainsolutions.drivie.common.ext.stringWithWhitespaceToIntNumber
import com.dvainsolutions.drivie.common.ext.toDate
import com.dvainsolutions.drivie.common.ext.toDateString
import com.dvainsolutions.drivie.common.snackbar.SnackbarManager
import com.dvainsolutions.drivie.data.model.FuelType
import com.dvainsolutions.drivie.data.model.Refueling
import com.dvainsolutions.drivie.data.model.convertInputToFuelType
import com.dvainsolutions.drivie.service.FirestoreService
import com.dvainsolutions.drivie.utils.Constants.SELECTED_CAR_ID
import com.dvainsolutions.drivie.utils.TextRecognition
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


@HiltViewModel
class RefuelViewModel @Inject constructor(
    private val application: Application,
    private val firestoreService: FirestoreService,
    private val dataStore: DataStore<Preferences>,
    private val context: Application
) : ViewModel() {

    var uiState = mutableStateOf(RefuelUiState())
        private set
    var isUnsaved = mutableStateOf(false)

    var hasCameraImage by mutableStateOf(false)
    val cameraResultUriForTextRecognition = mutableStateOf<Uri?>(null)

    private var _isButtonLoading = mutableStateOf(false)
    val isButtonLoading: State<Boolean> = _isButtonLoading

    var locations = mutableListOf<Address>()
        private set
    var vehicleList = mapOf<String, String>()
        private set

    private val fuelType get() = uiState.value.type
    private val carName get() = uiState.value.vehicle
    private val date get() = uiState.value.date
    private val location get() = uiState.value.location
    private val quantity get() = uiState.value.quantity
    private val cost get() = uiState.value.cost
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

    fun onLocationChange(value: String) {
        uiState.value = uiState.value.copy(location = value)

        if (value.length > 5) {
            viewModelScope.launch {
                getAddress()
            }
        }
        if (value.isEmpty()) {
            locations.clear()
        }
    }

    fun selectAddress(address: String) {
        locations.clear()
        uiState.value = uiState.value.copy(location = "").copy(location = address)
    }

    private fun getAddress() {
        val geocoder = Geocoder(context, Locale("hu"))
        if (Build.VERSION.SDK_INT >= 33) {
            val geocodeListener = Geocoder.GeocodeListener { addresses ->
                locations = addresses
            }
            geocoder.getFromLocationName(
                uiState.value.location,
                5,
                0.0,
                0.0,
                0.0,
                0.0,
                geocodeListener
            )
        } else {
            locations = geocoder.getFromLocationName(uiState.value.location, 5) as MutableList<Address>
        }
    }

    fun onFuelTypeChange(value: String) {
        uiState.value = uiState.value.copy(type = value)
    }

    fun onFuelQuantityChange(value: String) {
        uiState.value = uiState.value.copy(quantity = value)
    }

    fun onCostChange(value: String) {
        uiState.value = uiState.value.copy(cost = value)
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

    fun saveRefuelData(onNavigation: (id: String) -> Unit) {
        viewModelScope.launch {
            _isButtonLoading.value = true
            val refueling = Refueling(
                vehicle = carName,
                date = Timestamp(date.toDate() ?: Calendar.getInstance().time),
                location = location,
                type = FuelType.values()
                    .first { it == convertInputToFuelType(fuelType) },
                quantity = if (quantity.contains(".")) quantity.toFloatOrNull() else stringWithCommaToFloatNumber(quantity),
                cost = if (cost.contains(" ")) stringWithWhitespaceToIntNumber(cost) else cost.toIntOrNull(),
                mileage = mileage.toIntOrNull(),
                id = ""
            )

            vehicleList[carName]?.let {
                firestoreService.saveRefueling(value = refueling, carId = it) { task ->
                    if (task.exception == null) {
                        onNavigation.invoke(task.result.id)
                    } else {
                        _isButtonLoading.value = false
                        SnackbarManager.onError(task.exception!!)
                    }
                }
            }
        }
    }

    fun shouldShowClosePromptDialog(): Boolean {
        isUnsaved.value = when {
            date.isNotBlank() -> true
            location.isNotBlank() -> true
            quantity.isNotBlank() -> true
            cost.isNotBlank() -> true
            mileage.isNotBlank() -> true
            else -> {
                false
            }
        }
        return isUnsaved.value
    }

    fun runTextRecognition() {
        cameraResultUriForTextRecognition.value.let { res ->
            if (res != null) {
                TextRecognition(application.baseContext).runTextRecognition(res) { blocks ->
                    blocks.forEach { block ->
                        val quantityRegex = "^(\\d+\\,)?\\d+\\sL\$".toRegex()
                        val priceRegex = "^(\\d+\\s)?\\d+\\sFt\$".toRegex()
                        val dieselRegex = "\\W*((?i)diesel(?-i))\\W*".toRegex()
                        val petrolRegex = "\\W*((?i)benzin(?-i))\\W*".toRegex()
                        val dateRegex = "^\\d{4}\\.(0?[1-9]|1[012])\\.(0?[1-9]|[12][0-9]|3[01])\$".toRegex()
                        val blockList = block.text.split('\n')

                        blockList.forEach {
                            if (it.matches(quantityRegex)) {
                                uiState.value = uiState.value.copy(quantity = "").copy(quantity = it.removeSuffix("L").trim())
                            }
                            if (it.matches(priceRegex)) {
                                uiState.value = uiState.value.copy(cost = "").copy(cost = it.removeSuffix("Ft").trim())
                            }
                            if (it.matches(dateRegex)) {
                                uiState.value = uiState.value.copy(date = "").copy(date = it.trim())
                            }
                            if (dieselRegex.containsMatchIn(it) || petrolRegex.containsMatchIn(it)) {
                               it.split(" ").forEach { word ->
                                   if (word.uppercase() == "DIESEL" || word.uppercase() == "BENZIN") {
                                       uiState.value = uiState.value.copy(type = "").copy(type = convertInputToFuelType(word)?.getLabel(application) ?: "")
                                   }
                               }
                            }
                        }
                    }
                }
            }
        }
    }
}