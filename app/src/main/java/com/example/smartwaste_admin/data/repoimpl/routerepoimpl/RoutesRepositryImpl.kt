package com.example.smartwaste_admin.data.repoimpl.routerepoimpl

import com.example.smartwaste_admin.common.ROUTES_PATH
import com.example.smartwaste_admin.common.ResultState
import com.example.smartwaste_admin.data.models.AreaInfo
import com.example.smartwaste_admin.data.models.AreaProgress
import com.example.smartwaste_admin.data.models.RouteModel
import com.example.smartwaste_admin.data.models.RouteProgressModel
import com.example.smartwaste_admin.domain.repo.routesrepo.RoutesRepositry
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RoutesRepositryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : RoutesRepositry {

    override suspend fun getAllRoutes(): Flow<ResultState<List<RouteModel>>> = callbackFlow {
        trySend(ResultState.Loading)

        val listenerRegistration = firestore.collection(ROUTES_PATH)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    trySend(ResultState.Error(exception.message ?: "Unknown error"))
                    return@addSnapshotListener
                }

                val routes = snapshot?.documents?.mapNotNull {
                    it.toObject(RouteModel::class.java)?.copy(
                        id = it.id
                    )
                } ?: emptyList()

                trySend(ResultState.Success(routes))
            }

        awaitClose { listenerRegistration.remove() }
    }

    override suspend fun getRouteById(id: String): Flow<ResultState<RouteModel>> = callbackFlow {
        trySend(ResultState.Loading)

        val listenerRegistration = firestore.collection(ROUTES_PATH).document(id)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    trySend(ResultState.Error(exception.message ?: "Unknown error"))
                    return@addSnapshotListener
                }

                val route = snapshot?.toObject(RouteModel::class.java)
                if (route != null) {
                    trySend(ResultState.Success(route))
                } else {
                    trySend(ResultState.Error("Route not found"))
                }
            }

        awaitClose { listenerRegistration.remove() }
    }

    override suspend fun addRoutes(route: RouteModel): ResultState<String> {
        return try {
            val docRef = firestore.collection(ROUTES_PATH).document()
            val routeWithId = route.copy(id = docRef.id)
            docRef.set(routeWithId).await()

            createInitialRouteProgress(routeWithId)

            ResultState.Success("Route and progress created successfully")
        } catch (e: Exception) {
            ResultState.Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun updateRoutes(route: RouteModel): ResultState<String> {
        return try {
            firestore.collection(ROUTES_PATH).document(route.id).set(route).await()


            updateRouteProgressFromRoute(route)

            ResultState.Success("Route and progress updated successfully")
        } catch (e: Exception) {
            ResultState.Error(e.message ?: "Unknown error")
        }
    }

    private suspend fun createInitialRouteProgress(route: RouteModel) {
        val progress = RouteProgressModel(
            routeId = route.id,
            date = getTodayDateString(),
            assignedCollectorId = "",
            assignedTruckId = "",
            assignedDriverId = "",
            areaProgress = route.areaList.map { areaInfo ->
                AreaProgress(
                    areaId = areaInfo.areaId,
                    areaName = areaInfo.areaName,
                    isCompleted = false
                )
            },
            isRouteCompleted = false,
            lastUpdated = System.currentTimeMillis()
        )

        firestore.collection("route_progress").document(route.id).set(progress).await()
    }

    private fun getTodayDateString(): String {
        val formatter = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        return formatter.format(java.util.Date())
    }

    private suspend fun updateRouteProgressFromRoute(updatedRoute: RouteModel) {
        val progressRef = firestore.collection("route_progress").document(updatedRoute.id)
        val snapshot = progressRef.get().await()

        if (snapshot.exists()) {
            val oldProgress = snapshot.toObject(RouteProgressModel::class.java)

            if (oldProgress != null) {
                val updatedAreasProgress = updatedRoute.areaList.map { areaInfo ->
                    val existingArea = oldProgress.areaProgress.find { it.areaId == areaInfo.areaId }
                    existingArea ?: AreaProgress(
                        areaId = areaInfo.areaId,
                        areaName = areaInfo.areaName,
                        isCompleted = false
                    )
                }

                val newProgress = oldProgress.copy(
                    areaProgress = updatedAreasProgress,
                    lastUpdated = System.currentTimeMillis()
                )

                progressRef.set(newProgress).await()
            }
        } else {
            createInitialRouteProgress(updatedRoute)
        }
    }
}