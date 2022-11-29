package com.dvainsolutions.drivie.service

import com.dvainsolutions.drivie.data.model.*
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference

interface FirestoreService {
    fun createUser(user: User, onResult: (Throwable?) -> Unit)
    fun getCurrentUserData(onResult: (User) -> Unit, onError: (Throwable?) -> Unit)
    fun updateUser(userId: String, value: HashMap<String, Any>, onResult: (Throwable?) -> Unit)
    fun createVehicle(userId: String, value: Vehicle, onResult: (Task<DocumentReference>) -> Unit)
    fun updateVehicle(userId: String, vehicleId: String, value: HashMap<String, Any>, onResult: (Throwable?) -> Unit)
    fun getVehicleNameListWithId(onResult: (Map<String, String>) -> Unit, onError: (Throwable?) -> Unit)
    fun saveRefueling(value: Refueling, carId: String, onResult: (Task<DocumentReference>) -> Unit)
    fun getVehicleList(onResult: (List<Vehicle>) -> Unit, onError: (Throwable?) -> Unit)
    fun getVehicleDetails(carId: String, onResult: (Vehicle) -> Unit, onError: (Throwable?) -> Unit)
    fun updateVehicleDetails(carId: String, vehicle: Vehicle, onResult: (Throwable?) -> Unit)
    fun getRefuelDetails(carId: String, refuelId: String, onResult: (Refueling) -> Unit, onError: (Throwable?) -> Unit)
    fun getRefuels(carId: String, onResult: (List<Refueling>?) -> Unit, onError: (Throwable?) -> Unit)
    fun getParts(carId: String, onResult: (List<VehiclePart>?) -> Unit, onError: (Throwable?) -> Unit)
    fun updateParts(carId: String, value: List<VehiclePart>, onResult: (Throwable?) -> Unit)
    fun saveTrip(trip: Trip, carId: String, onResult: (Task<DocumentReference>) -> Unit)
    fun getTrips(carId: String, onResult: (List<Trip>?) -> Unit, onError: (Throwable?) -> Unit)
    fun getTripDetails(carId: String, tripId: String, onResult: (Trip) -> Unit, onError: (Throwable?) -> Unit)
    fun saveService(service: Service, carId: String, onResult: (Task<DocumentReference>) -> Unit)
    fun getServices(carId: String, onResult: (List<Service>?) -> Unit, onError: (Throwable?) -> Unit)
    fun getServiceDetails(carId: String, serviceId: String, onResult: (Service) -> Unit, onError: (Throwable?) -> Unit)
    fun getAvgDataFromTrips(carId: String, onResult: (List<String?>) -> Unit, onError: (Throwable?) -> Unit)
    fun saveMiscData(miscData: MiscData, carId: String, onResult: (Throwable?) -> Unit)
    fun getAllMiscData(carId: String, onResult: (List<MiscData>?) -> Unit, onError: (Throwable?) -> Unit)
}