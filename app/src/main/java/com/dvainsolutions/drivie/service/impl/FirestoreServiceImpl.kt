package com.dvainsolutions.drivie.service.impl

import com.dvainsolutions.drivie.data.model.*
import com.dvainsolutions.drivie.service.FirestoreService
import com.dvainsolutions.drivie.utils.Constants.MISC_DATA_SUB_DOCUMENT
import com.dvainsolutions.drivie.utils.Constants.REFUELINGS_SUB_DOCUMENT
import com.dvainsolutions.drivie.utils.Constants.SERVICES_SUB_DOCUMENT
import com.dvainsolutions.drivie.utils.Constants.TRIPS_SUB_DOCUMENT
import com.dvainsolutions.drivie.utils.Constants.USERS_DOCUMENT
import com.dvainsolutions.drivie.utils.Constants.VEHICLES_SUB_DOCUMENT
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.DecimalFormat
import java.util.*
import javax.inject.Inject

class FirestoreServiceImpl @Inject constructor() : FirestoreService {

    private val firestore = Firebase.firestore

    override fun createUser(user: User, onResult: (Throwable?) -> Unit) {
        firestore.collection(USERS_DOCUMENT)
            .document(Firebase.auth.currentUser?.uid ?: UUID.randomUUID().toString())
            .set(user)
            .addOnCompleteListener { onResult(it.exception) }
    }

    override fun getCurrentUserData(
        onResult: (User) -> Unit,
        onError: (Throwable?) -> Unit
    ) {
        firestore.collection(USERS_DOCUMENT).document(Firebase.auth.currentUser?.uid!!).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val result = it.result.toObject(User::class.java)
                    if (result != null) {
                        onResult(result)
                    }
                } else {
                    onError(it.exception)
                }
            }
    }

    override fun updateUser(
        userId: String,
        value: HashMap<String, Any>,
        onResult: (Throwable?) -> Unit
    ) {
        firestore
            .collection(USERS_DOCUMENT)
            .document(userId).update(value)
            .addOnCompleteListener {
                onResult(it.exception)
            }
    }

    override fun createVehicle(
        userId: String,
        value: Vehicle,
        onResult: (Task<DocumentReference>) -> Unit
    ) {
        firestore
            .collection(USERS_DOCUMENT)
            .document(userId).collection(VEHICLES_SUB_DOCUMENT).add(value)
            .addOnCompleteListener {
                onResult(it)
            }
    }

    override fun updateVehicle(
        userId: String,
        vehicleId: String,
        value: HashMap<String, Any>,
        onResult: (Throwable?) -> Unit
    ) {
        firestore
            .collection(USERS_DOCUMENT)
            .document(userId).collection(VEHICLES_SUB_DOCUMENT).document(vehicleId).update(value)
            .addOnCompleteListener {
                onResult(it.exception)
            }
    }

    override fun getVehicleNameListWithId(
        onResult: (Map<String, String>) -> Unit,
        onError: (Throwable?) -> Unit
    ) {
        val vehicleList: MutableMap<String, String> = mutableMapOf()
        firestore
            .collection(USERS_DOCUMENT)
            .document(Firebase.auth.currentUser?.uid ?: "").collection(VEHICLES_SUB_DOCUMENT).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        vehicleList[document.getString("nickname") ?: document.getString("licence")
                        ?: ""] =
                            document.getString("id") ?: ""
                    }
                    onResult(vehicleList)
                } else {
                    onError(task.exception)
                }
            }
    }

    override fun saveRefueling(
        value: Refueling,
        carId: String,
        onResult: (Task<DocumentReference>) -> Unit
    ) {
        firestore
            .collection(USERS_DOCUMENT)
            .document(Firebase.auth.currentUser?.uid ?: "")
            .collection(VEHICLES_SUB_DOCUMENT)
            .document(carId)
            .collection(REFUELINGS_SUB_DOCUMENT)
            .add(value)
            .addOnCompleteListener { task ->
                task.result.update("id", task.result.id).addOnCompleteListener {
                    onResult(task)
                }
            }
    }

    override fun getVehicleList(onResult: (List<Vehicle>) -> Unit, onError: (Throwable?) -> Unit) {
        firestore
            .collection(USERS_DOCUMENT)
            .document(Firebase.auth.currentUser?.uid ?: "")
            .collection(VEHICLES_SUB_DOCUMENT)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.result != null) {
                        val result = task.result.toObjects(Vehicle::class.java)
                        onResult(result)
                    }
                } else {
                    onError(task.exception)
                }
            }
    }

    override fun getVehicleDetails(
        carId: String,
        onResult: (Vehicle) -> Unit,
        onError: (Throwable?) -> Unit
    ) {
        firestore
            .collection(USERS_DOCUMENT)
            .document(Firebase.auth.currentUser?.uid ?: "")
            .collection(VEHICLES_SUB_DOCUMENT)
            .document(carId)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.result != null) {
                        val result = task.result.toObject(Vehicle::class.java)
                        if (result != null) {
                            onResult(result)
                        }
                    }
                } else {
                    onError(task.exception)
                }
            }
    }

    override fun updateVehicleDetails(
        carId: String,
        vehicle: Vehicle,
        onResult: (Throwable?) -> Unit
    ) {
        firestore
            .collection(USERS_DOCUMENT)
            .document(Firebase.auth.currentUser?.uid ?: "").collection(VEHICLES_SUB_DOCUMENT)
            .document(carId)
            .set(vehicle)
            .addOnCompleteListener { task ->
                onResult(task.exception)
            }
    }

    override fun getRefuelDetails(
        carId: String,
        refuelId: String,
        onResult: (Refueling) -> Unit,
        onError: (Throwable?) -> Unit
    ) {
        firestore
            .collection(USERS_DOCUMENT)
            .document(Firebase.auth.currentUser?.uid ?: "")
            .collection(VEHICLES_SUB_DOCUMENT)
            .document(carId)
            .collection(REFUELINGS_SUB_DOCUMENT)
            .document(refuelId)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.result != null) {
                        val result = task.result.toObject(Refueling::class.java)
                        if (result != null) {
                            onResult(result)
                        }
                    }
                } else {
                    onError(task.exception)
                }
            }
    }

    override fun getRefuels(
        carId: String,
        onResult: (List<Refueling>?) -> Unit,
        onError: (Throwable?) -> Unit
    ) {
        val refuelList: MutableList<Refueling> = mutableListOf()
        firestore
            .collection(USERS_DOCUMENT)
            .document(Firebase.auth.currentUser?.uid ?: "")
            .collection(VEHICLES_SUB_DOCUMENT)
            .document(carId)
            .collection(REFUELINGS_SUB_DOCUMENT)
            .orderBy("date", Query.Direction.DESCENDING)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        refuelList.add(document.toObject(Refueling::class.java))
                    }
                    onResult(refuelList)
                }
            }
    }

    @Suppress("UNCHECKED_CAST")
    override fun getParts(
        carId: String,
        onResult: (List<VehiclePart>?) -> Unit,
        onError: (Throwable?) -> Unit
    ) {
        firestore
            .collection(USERS_DOCUMENT)
            .document(Firebase.auth.currentUser?.uid ?: "")
            .collection(VEHICLES_SUB_DOCUMENT)
            .document(carId)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val parts = task.result.toObject(VehiclePartListHelper::class.java)?.parts
                    onResult(parts)
                } else {
                    onError(task.exception)
                }
            }
    }

    override fun updateParts(
        carId: String,
        value: List<VehiclePart>,
        onResult: (Throwable?) -> Unit
    ) {
        getParts(
            carId = carId,
            onResult = { vehicleParts ->
                val docIdRef = firestore
                    .collection(USERS_DOCUMENT)
                    .document(Firebase.auth.currentUser?.uid ?: "")
                    .collection(VEHICLES_SUB_DOCUMENT)
                    .document(carId)

                value.forEachIndexed { index, newPart ->
                    var isCompleted = false

                    if (vehicleParts == null) {
                        docIdRef.update("parts", FieldValue.arrayUnion(newPart))
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    isCompleted = true
                                } else {
                                    onResult(task.exception)
                                }
                            }
                    } else {
                        docIdRef
                            .update(
                                "parts",
                                FieldValue.arrayRemove(vehicleParts.find { it.name == newPart.name })
                            )
                            .addOnCompleteListener { _ ->
                                if (vehicleParts.any { it.name == newPart.name }) {
                                    vehicleParts.find { it.name == newPart.name }?.let {
                                        it.currentHealth = newPart.maxLifeSpan
                                        it.replacementTime = newPart.replacementTime

                                        docIdRef.update("parts", FieldValue.arrayUnion(it))
                                            .addOnCompleteListener { task ->
                                                if (task.isSuccessful) {
                                                    isCompleted = true
                                                } else {
                                                    onResult(task.exception)
                                                }
                                            }
                                    }
                                } else {
                                    docIdRef.update("parts", FieldValue.arrayUnion(newPart))
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                isCompleted = true
                                            } else {
                                                onResult(task.exception)
                                            }
                                        }
                                }
                            }
                    }
                    if (index == value.size - 1 && isCompleted) {
                        onResult(null)
                    }
                }
            },
            onError = {

            }
        )
    }

    override fun saveTrip(trip: Trip, carId: String, onResult: (Task<DocumentReference>) -> Unit) {
        firestore
            .collection(USERS_DOCUMENT)
            .document(Firebase.auth.currentUser?.uid ?: "")
            .collection(VEHICLES_SUB_DOCUMENT)
            .document(carId)
            .collection(TRIPS_SUB_DOCUMENT)
            .add(trip)
            .addOnCompleteListener { task ->
                task.result.update("id", task.result.id).addOnCompleteListener {
                    onResult(task)
                }
            }
    }

    override fun getTrips(
        carId: String,
        onResult: (List<Trip>?) -> Unit,
        onError: (Throwable?) -> Unit
    ) {
        val tripList: MutableList<Trip> = mutableListOf()

        firestore
            .collection(USERS_DOCUMENT)
            .document(Firebase.auth.currentUser?.uid ?: "")
            .collection(VEHICLES_SUB_DOCUMENT)
            .document(carId)
            .collection(TRIPS_SUB_DOCUMENT)
            .orderBy("date", Query.Direction.DESCENDING)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        tripList.add(document.toObject(Trip::class.java))
                    }
                    onResult(tripList)
                } else {
                    onError(task.exception)
                }
            }
    }

    override fun getTripDetails(
        carId: String,
        tripId: String,
        onResult: (Trip) -> Unit,
        onError: (Throwable?) -> Unit
    ) {
        firestore
            .collection(USERS_DOCUMENT)
            .document(Firebase.auth.currentUser?.uid ?: "")
            .collection(VEHICLES_SUB_DOCUMENT)
            .document(carId)
            .collection(TRIPS_SUB_DOCUMENT)
            .document(tripId)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.result != null) {
                        val result = task.result.toObject(Trip::class.java)
                        if (result != null) {
                            onResult(result)
                        }
                    }
                } else {
                    onError(task.exception)
                }
            }
    }

    override fun saveService(
        service: Service,
        carId: String,
        onResult: (Task<DocumentReference>) -> Unit
    ) {
        firestore
            .collection(USERS_DOCUMENT)
            .document(Firebase.auth.currentUser?.uid ?: "")
            .collection(VEHICLES_SUB_DOCUMENT)
            .document(carId)
            .collection(SERVICES_SUB_DOCUMENT)
            .add(service)
            .addOnCompleteListener { task ->
                task.result.update("id", task.result.id).addOnCompleteListener {
                    onResult(task)
                }
            }
    }

    override fun getServices(
        carId: String,
        onResult: (List<Service>?) -> Unit,
        onError: (Throwable?) -> Unit
    ) {
        val serviceList: MutableList<Service> = mutableListOf()

        firestore
            .collection(USERS_DOCUMENT)
            .document(Firebase.auth.currentUser?.uid ?: "")
            .collection(VEHICLES_SUB_DOCUMENT)
            .document(carId)
            .collection(SERVICES_SUB_DOCUMENT)
            .orderBy("date", Query.Direction.DESCENDING)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        serviceList.add(document.toObject(Service::class.java))
                    }
                    onResult(serviceList)
                } else {
                    onError(task.exception)
                }
            }
    }

    override fun getServiceDetails(
        carId: String,
        serviceId: String,
        onResult: (Service) -> Unit,
        onError: (Throwable?) -> Unit
    ) {
        firestore
            .collection(USERS_DOCUMENT)
            .document(Firebase.auth.currentUser?.uid ?: "")
            .collection(VEHICLES_SUB_DOCUMENT)
            .document(carId)
            .collection(SERVICES_SUB_DOCUMENT)
            .document(serviceId)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.result != null) {
                        val result = task.result.toObject(Service::class.java)
                        if (result != null) {
                            onResult(result)
                        }
                    }
                } else {
                    onError(task.exception)
                }
            }
    }

    override fun getAvgDataFromTrips(
        carId: String,
        onResult: (List<String?>) -> Unit,
        onError: (Throwable?) -> Unit
    ) {
        getTrips(
            carId,
            onResult = { trips ->
                val decimalFormat = DecimalFormat("#.##")

                getRefuels(
                    carId = carId,
                    onResult = { refuelings ->
                        onResult(
                            listOf(
                                decimalFormat.format(trips?.map { it.consumption }?.average()),
                                decimalFormat.format(trips?.map { it.distance }?.average()),
                                decimalFormat.format(refuelings?.map {
                                    it.quantity?.toDouble() ?: 0.0
                                }?.average()),
                                decimalFormat.format(refuelings?.map { it.cost?.toDouble() ?: 0.0 }
                                    ?.average())
                            )
                        )
                    },
                    onError = onError
                )
            },
            onError = onError
        )
    }

    override fun saveMiscData(miscData: MiscData, carId: String, onResult: (Throwable?) -> Unit) {
        firestore
            .collection(USERS_DOCUMENT)
            .document(Firebase.auth.currentUser?.uid ?: "")
            .collection(VEHICLES_SUB_DOCUMENT)
            .document(carId)
            .collection(MISC_DATA_SUB_DOCUMENT)
            .add(miscData)
            .addOnCompleteListener { task ->
                task.result.update("id", task.result.id).addOnCompleteListener {
                    onResult(task.exception)
                }
            }
    }

    override fun getAllMiscData(
        carId: String,
        onResult: (List<MiscData>?) -> Unit,
        onError: (Throwable?) -> Unit
    ) {
        val miscDataList: MutableList<MiscData> = mutableListOf()

        firestore
            .collection(USERS_DOCUMENT)
            .document(Firebase.auth.currentUser?.uid ?: "")
            .collection(VEHICLES_SUB_DOCUMENT)
            .document(carId)
            .collection(MISC_DATA_SUB_DOCUMENT)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        miscDataList.add(document.toObject(MiscData::class.java))
                    }
                    onResult(miscDataList)
                } else {
                    onError(task.exception)
                }
            }
    }

    override fun getMiscDetails(
        carId: String,
        miscId: String,
        onResult: (MiscData?) -> Unit,
        onError: (Throwable?) -> Unit
    ) {
        firestore
            .collection(USERS_DOCUMENT)
            .document(Firebase.auth.currentUser?.uid ?: "")
            .collection(VEHICLES_SUB_DOCUMENT)
            .document(carId)
            .collection(MISC_DATA_SUB_DOCUMENT)
            .document(miscId)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.result != null) {
                        val result = task.result.toObject(MiscData::class.java)
                        if (result != null) {
                            onResult(result)
                        }
                    }
                } else {
                    onError(task.exception)
                }
            }
    }
}