package com.example.smartwaste_admin.data.repoimpl.arearepositryimpl

import com.example.smartwaste_admin.common.AREA_PATH
import com.example.smartwaste_admin.common.ResultState
import com.example.smartwaste_admin.data.di.DataModule_ProvideFirebaseFireStoreFactory
import com.example.smartwaste_admin.data.models.AreaModel
import com.example.smartwaste_admin.data.models.NominatimPlace
import com.example.smartwaste_admin.data.remote.NominatimApi
import com.example.smartwaste_admin.data.remote.RetrofitInstance
import com.example.smartwaste_admin.domain.repo.arearepo.AreaRepositry
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AreaRepositryImpl @Inject constructor(
    private val firebaseFireStore: FirebaseFirestore
) : AreaRepositry {

    val retrofitInstane = RetrofitInstance
    override suspend fun getAllAreas(): Flow<ResultState<List<AreaModel>>> = callbackFlow {

        trySend(ResultState.Loading)

        try {
            firebaseFireStore.collection(AREA_PATH).addSnapshotListener { value, error ->


                if (error != null) {
                    trySend(ResultState.Error(error.message ?: "Unknown error"))
                    return@addSnapshotListener
                }

                val areas = value?.documents?.mapNotNull {
                    it.toObject(AreaModel::class.java)?.copy(
                        areadId = it.id
                    )
                } ?: emptyList()
                trySend(ResultState.Success(areas))
            }
        } catch (e: Exception) {
            trySend(ResultState.Error(e.message ?: "Unknown error"))
        }


        awaitClose {
            close()
        }
    }

    override suspend fun addArea(area: AreaModel): ResultState<String> {
        return try {
            val docRef = firebaseFireStore.collection(AREA_PATH).document()

            val areaWithId = area.copy(areadId = docRef.id)

            docRef.set(areaWithId).await()

            ResultState.Success("Area added successfully")
        } catch (e: Exception) {
            ResultState.Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun getAddressDetails(query: String): Flow<ResultState<List<NominatimPlace>>> =callbackFlow{
        trySend(ResultState.Loading)

        try {
            val data=retrofitInstane.api.searchPlace(query=query)

            trySend(ResultState.Success(data))
        }catch (e: Exception){
            trySend(ResultState.Error(e.message.toString()))
        }

        awaitClose {
            close()
        }
    }


}