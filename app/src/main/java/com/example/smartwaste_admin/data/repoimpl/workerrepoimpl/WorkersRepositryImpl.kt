package com.example.smartwaste_admin.data.repoimpl.workerrepoimpl

import com.example.smartwaste_admin.common.ResultState
import com.example.smartwaste_admin.common.WORKER_PATH
import com.example.smartwaste_admin.data.models.WorkerModel
import com.example.smartwaste_admin.domain.repo.workerrepo.WorkersRepositry
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class WorkersRepositryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : WorkersRepositry {
    override suspend fun getAllWorkers(): Flow<ResultState<List<WorkerModel>>> = callbackFlow{
        trySend(ResultState.Loading)

        try {
            firestore.collection(WORKER_PATH).addSnapshotListener {
                value, error ->
                if (error != null) {
                    trySend(ResultState.Error(error.message.toString()))
                } else {
                    val workers = value?.documents?.mapNotNull {
                        it.toObject(WorkerModel::class.java)?.copy(
                            id = it.id
                        )

                    }
                    trySend(ResultState.Success(workers!!))
                }
            }
        }catch (
            e: Exception
        ) {
            trySend(ResultState.Error(e.message.toString()))
        }

        awaitClose {
            close()
        }
    }

    override suspend fun getWorkerById(id: String): Flow<ResultState<WorkerModel>> =callbackFlow {
        trySend(ResultState.Loading)

        try {

            firestore.collection(WORKER_PATH).document(id.toString()).addSnapshotListener{
                value, error ->
                if (error != null) {
                    trySend(ResultState.Error(error.message.toString()))
                } else {
                    val worker = value?.toObject(WorkerModel::class.java)
                    trySend(ResultState.Success(worker!!))


                }
            }
        }catch (e: Exception){
            trySend(ResultState.Error(e.message.toString()))

        }

        awaitClose {
            close()
        }

    }

}