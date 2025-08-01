package com.example.smartwaste_admin.data.repoimpl.residencerepoimpl

import androidx.compose.ui.graphics.RectangleShape
import com.example.smartwaste_admin.common.ResultState
import com.example.smartwaste_admin.common.USERS_PATH
import com.example.smartwaste_admin.data.models.ResidentModel
import com.example.smartwaste_admin.domain.repo.residentrepo.ResidentRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ResidentRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ResidentRepository{
    override suspend fun getAllresident(): Flow<ResultState<List<ResidentModel>>> = callbackFlow {
        trySend(ResultState.Loading)
        try {


            val listener = firestore.collection(USERS_PATH)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(ResultState.Error(error.message ?: "Unknown error"))
                        return@addSnapshotListener
                    }

                    val residents = snapshot?.documents?.mapNotNull {
                        it.toObject(ResidentModel::class.java)
                    } ?: emptyList()

                    trySend(ResultState.Success(residents))
                }

            awaitClose {
                listener.remove()
            }
        } catch (e: Exception) {
            trySend(ResultState.Error(e.message ?: "Unexpected error occurred"))
            close(e)
        }
    }

    override suspend fun getResidentById(id: String): Flow<ResultState<ResidentModel>> =callbackFlow {
        trySend(ResultState.Loading)

        try {


            val resident = firestore.collection(USERS_PATH).document(id).get().await()
                .toObject(ResidentModel::class.java)

            if (resident != null) {
                trySend(ResultState.Success(resident))
            } else {
                trySend(ResultState.Error("Resident not found"))
            }
        } catch (e: Exception) {
            trySend(ResultState.Error(e.message ?: "Unexpected error occurred"))
            close(e)

        }

    }

}