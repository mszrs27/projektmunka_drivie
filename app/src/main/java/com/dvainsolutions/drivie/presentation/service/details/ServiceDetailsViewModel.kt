package com.dvainsolutions.drivie.presentation.service.details

import android.util.Log
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
class ServiceDetailsViewModel @Inject constructor(
   private val firestoreService: FirestoreService,
   private val dataStore: DataStore<Preferences>
): ViewModel() {

   var serviceDetailsState by mutableStateOf(ServiceDetailsState())
      private set


   fun getDetails(serviceId: String?) {
      if (serviceId == null) return
      viewModelScope.launch {
         val preferences = dataStore.data.first()
         preferences[stringPreferencesKey(Constants.SELECTED_CAR_ID)]?.let { carId ->
            serviceDetailsState = serviceDetailsState.copy(isLoading = true)
            firestoreService.getServiceDetails(
               carId = carId,
               serviceId = serviceId,
               onResult = {
                  serviceDetailsState = serviceDetailsState.copy(data = it, isLoading = false)
                  Log.e("ALMA", "getDetails: ${it.replacedParts}", )
               },
               onError = {
                  serviceDetailsState = serviceDetailsState.copy(isLoading = false)
               }
            )
         }
      }
   }
}