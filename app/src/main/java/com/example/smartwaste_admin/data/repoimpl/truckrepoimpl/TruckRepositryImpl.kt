package com.example.smartwaste_admin.data.repoimpl.truckrepoimpl

import androidx.compose.ui.graphics.RectangleShape
import coil3.size.ViewSizeResolver
import com.example.smartwaste_admin.common.ResultState
import com.example.smartwaste_admin.common.TRUCKS_PATH
import com.example.smartwaste_admin.data.models.TruckModel
import com.example.smartwaste_admin.domain.repo.truckRepo.TrucksRepositry
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TruckRepositryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
)  : TrucksRepositry {
    override suspend fun getAllTrucks(): Flow<ResultState<List<TruckModel>>> = callbackFlow {
        try {
            trySend(ResultState.Loading)

            val listenerRegistration = firestore.collection(TRUCKS_PATH)
                .addSnapshotListener { value, error ->

                    if (error != null) {
                        trySend(ResultState.Error(error.message ?: "Unknown error"))
                        return@addSnapshotListener
                    }

                    val results = value?.documents?.mapNotNull {
                        it.toObject(TruckModel::class.java)?.copy(id = it.id)
                    } ?: emptyList()

                    trySend(ResultState.Success(results))
                }

            awaitClose {
                listenerRegistration.remove()
            }

        } catch (e: Exception) {
            trySend(ResultState.Error(e.message ?: "Unexpected error occurred"))
            close(e)
        }
    }
    override suspend fun AddTruck(truckModel: TruckModel): ResultState<String> {

       return try {
            firestore.collection(TRUCKS_PATH).document().set(truckModel).await()

            ResultState.Success("Truck Added")
        }catch (e: Exception){
            ResultState.Error(e.message.toString())
        }

    }

    override suspend fun deleteTruck(truckId: String): ResultState<String> {

        return try {
            firestore.collection(TRUCKS_PATH).document(truckId).delete().await()
            ResultState.Success("Truck Deleted")
        }catch (e: Exception){
            ResultState.Error(e.message.toString())
        }


    }

}