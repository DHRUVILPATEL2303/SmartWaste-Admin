package com.example.smartwaste_admin.data.repoimpl.reportrepositryimpl

import com.example.smartwaste_admin.common.REPORT_PATH
import com.example.smartwaste_admin.common.ResultState
import com.example.smartwaste_admin.data.models.ReportModel
import com.example.smartwaste_admin.domain.repo.reportrepo.ReportRepositry
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ReportRepositryImpl @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore
) : ReportRepositry {
    override suspend fun getAllReports(): Flow<ResultState<List<ReportModel>>> = callbackFlow {

        trySend(ResultState.Loading)

        try {
            firebaseFirestore.collection(REPORT_PATH).addSnapshotListener { snapshot, error ->

                if (error != null) {
                    trySend(ResultState.Error(error.message ?: "Unknown error occurred"))
                    return@addSnapshotListener
                }


                val reports = snapshot?.documents?.mapNotNull {
                    it.toObject(ReportModel::class.java).apply {
                        this?.reportId = it.id
                    }
                } ?: emptyList()
                trySend(ResultState.Success(reports))


            }
        } catch (e: Exception) {
            trySend(ResultState.Error(e.message ?: "Unknown error occurred"))

        }

        awaitClose {
            close()
        }
    }

    override suspend fun deleteReport(reportID: String): Flow<ResultState<String>> = flow {
        emit(ResultState.Loading)

        try {
            firebaseFirestore.collection(REPORT_PATH).document(reportID).delete().await()
            emit(ResultState.Success("Report deleted successfully"))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message ?: "Unknown error occurred"))

        }

    }


}