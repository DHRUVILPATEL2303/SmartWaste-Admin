package com.example.smartwaste_admin.data.repoimpl.holidayrepositryimpl

import com.example.smartwaste_admin.common.HOLIDAY_PATH
import com.example.smartwaste_admin.common.ResultState
import com.example.smartwaste_admin.data.models.HolidayModel
import com.example.smartwaste_admin.domain.repo.holidayrepo.HolidayRepositry
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class HolidayRepositryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : HolidayRepositry {
    override suspend fun getAllHolidays(): Flow<ResultState<List<HolidayModel>>> = callbackFlow {
        trySend(ResultState.Loading)

        val listener = firestore.collection(HOLIDAY_PATH)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    trySend(ResultState.Error(error.message ?: "Unknown error"))
                    return@addSnapshotListener
                }

                val holidays = value?.documents?.mapNotNull { doc ->
                    doc.toObject(HolidayModel::class.java)?.copy(id = doc.id)
                } ?: emptyList()

                trySend(ResultState.Success(holidays))
            }

        awaitClose {
            listener.remove()
        }
    }

    override suspend fun addHoliday(holidayModel: HolidayModel): ResultState<String> {
        return try {
            firestore.collection(HOLIDAY_PATH)
                .add(holidayModel)
                .await()
            ResultState.Success("Holiday added successfully")
        } catch (e: Exception) {
            ResultState.Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun updateHoliday(holidayModel: HolidayModel): ResultState<String> {
        return try {
            firestore.collection(HOLIDAY_PATH)
                .document(holidayModel.id)
                .set(holidayModel)
                .await()

            ResultState.Success("Holiday updated successfully.")
        } catch (e: Exception) {
            ResultState.Error(e.localizedMessage ?: "Unknown error occurred.")
        }
    }
}