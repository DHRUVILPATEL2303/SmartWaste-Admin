package com.example.smartwaste_admin.data.repoimpl.extraservicerepoimpl

import android.util.Log
import com.example.smartwaste_admin.common.EXTRA_SERVICE_PATH
import com.example.smartwaste_admin.common.ResultState
import com.example.smartwaste_admin.data.models.ExtraServiceModel
import com.example.smartwaste_admin.domain.repo.extraservicerepo.ExtraServiceRepositry
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ExtraServiceRepositryImpl @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore,
) : ExtraServiceRepositry{
    override suspend fun getAllService(): Flow<ResultState<List<ExtraServiceModel>>> = callbackFlow {
        trySend(ResultState.Loading)

        try {
            val allServices = mutableListOf<ExtraServiceModel>()

            Log.d("ExtraServiceRepo", "Starting to fetch all services")

            val querySnapshot = firebaseFirestore
                .collectionGroup(EXTRA_SERVICE_PATH)
                .get()
                .await()

            Log.d("ExtraServiceRepo", "Found ${querySnapshot.documents.size} documents in collection group")

            for (document in querySnapshot.documents) {
                try {
                    Log.d("ExtraServiceRepo", "Processing document: ${document.id}")
                    Log.d("ExtraServiceRepo", "Document data: ${document.data}")

                    val service = document.toObject(ExtraServiceModel::class.java)
                    if (service != null) {
                        val serviceWithId = service.copy(id = document.id)
                        allServices.add(serviceWithId)
                        Log.d("ExtraServiceRepo", "Successfully added service: ${serviceWithId.id}")
                    } else {
                        Log.w("ExtraServiceRepo", "Failed to convert document ${document.id} to ExtraServiceModel")
                    }
                } catch (e: Exception) {
                    Log.e("ExtraServiceRepo", "Error processing document ${document.id}: ${e.message}", e)
                }
            }

            Log.d("ExtraServiceRepo", "Total services found: ${allServices.size}")
            trySend(ResultState.Success(allServices))

        } catch (e: Exception) {
            Log.e("ExtraServiceRepo", "Error fetching services: ${e.message}", e)
            trySend(ResultState.Error(e.message ?: "Unknown error occurred while fetching services"))
        }

        awaitClose { close() }
    }

    override suspend fun deleteExtraService(userId: String, id: String): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)

        try {
            firebaseFirestore.collection(EXTRA_SERVICE_PATH)
                .document(userId)
                .collection(EXTRA_SERVICE_PATH)
                .document(id)
                .delete()
                .await()

            trySend(ResultState.Success("Service deleted successfully"))
        } catch (e: Exception) {
            trySend(ResultState.Error(e.message ?: "error"))
        }

        awaitClose { close() }
    }

    override suspend fun updateStatusExtraService(
        userId: String,
        id: String,
        status: String
    ): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)

        try {
            firebaseFirestore.collection(EXTRA_SERVICE_PATH)
                .document(userId)
                .collection(EXTRA_SERVICE_PATH)
                .document(id)
                .update("status", status)
                .await()

            trySend(ResultState.Success("Status updated successfully"))
        } catch (e: Exception) {
            trySend(ResultState.Error(e.message ?:"Something went wrong"))
        }

        awaitClose { close() }
    }
}