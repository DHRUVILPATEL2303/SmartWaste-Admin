package com.example.smartwaste_admin.data.repoimpl.routerepoimpl

import com.example.smartwaste_admin.common.ROUTES_PATH
import com.example.smartwaste_admin.common.ResultState
import com.example.smartwaste_admin.data.models.RouteModel
import com.example.smartwaste_admin.domain.repo.routesrepo.RoutesRepositry
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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
                    it.toObject(RouteModel::class.java)
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
}