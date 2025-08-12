package com.example.smartwaste_admin.data.repoimpl.routeprogressrepoimpl

import com.example.smartwaste_admin.common.ROUTE_PROGRESS_PATH
import com.example.smartwaste_admin.common.ResultState
import com.example.smartwaste_admin.data.models.RouteProgressModel
import com.example.smartwaste_admin.domain.repo.routeprogressrepositry.RouteProgressRepositry
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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

}