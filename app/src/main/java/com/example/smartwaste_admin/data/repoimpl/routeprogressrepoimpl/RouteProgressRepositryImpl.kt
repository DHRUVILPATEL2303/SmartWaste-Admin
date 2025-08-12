package com.example.smartwaste_admin.data.repoimpl.routeprogressrepoimpl

import com.example.smartwaste_admin.common.ROUTE_PROGRESS_PATH
import com.example.smartwaste_admin.common.ResultState
import com.example.smartwaste_admin.data.models.AreaProgress
import com.example.smartwaste_admin.data.models.RouteProgressModel
import com.example.smartwaste_admin.domain.repo.routeprogressrepositry.RouteProgressRepositry
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.WriteBatch
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RouteProgressRepositryImpl @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore
) : RouteProgressRepositry {
    override suspend fun getAllRoutesProgress(): Flow<ResultState<List<RouteProgressModel>>> =
        callbackFlow {

            trySend(ResultState.Loading)

            try {
                firebaseFirestore.collection(ROUTE_PROGRESS_PATH)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            trySend(ResultState.Error(error.message.toString()))

                        }

                        if (snapshot != null) {
                            val routeProgressList =
                                snapshot.toObjects(RouteProgressModel::class.java)
                            trySend(ResultState.Success(routeProgressList))
                        }
                    }
            } catch (e: Exception) {
                trySend(ResultState.Error(e.message.toString()))

            }

            awaitClose {
                close()
            }
        }



    override suspend fun forceResetAllRoutes(): Flow<ResultState<Unit>> = flow{
         try {
            val routesSnapshot = firebaseFirestore.collection(ROUTE_PROGRESS_PATH).get().await()

            if (routesSnapshot.isEmpty) {
                emit(ResultState.Success(Unit)) // No routes to reset
            }

            val batch: WriteBatch = firebaseFirestore.batch()

            for (document in routesSnapshot.documents) {
                val routeData = document.toObject(RouteProgressModel::class.java) ?: continue

                val resetAreaProgress = routeData.areaProgress.map { it.copy(isCompleted = false, completedAt = null) }

                val updates = mapOf(
                    "assignedCollectorId" to "",
                    "assignedDriverId" to "",
                    "assignedTruckId" to "",
                    "isRouteCompleted" to false,
                    "areaProgress" to resetAreaProgress.map { it.toMap() },
                    "lastUpdated" to System.currentTimeMillis()
                )
                batch.update(document.reference, updates)
            }

            batch.commit().await()
           emit(ResultState.Success(Unit))
         } catch (e: Exception) {
             emit(ResultState.Error(e.message.toString()))

        }
    }

    private fun AreaProgress.toMap(): Map<String, Any?> {
        return mapOf(
            "areaId" to areaId,
            "areaName" to areaName,
            "completed" to isCompleted,
            "completedAt" to completedAt
        )
    }



}