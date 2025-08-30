package com.example.smartwaste_admin.presentation.viewmodels.ediorialviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartwaste_admin.common.ResultState
import com.example.smartwaste_admin.data.models.EdiorialModel
import com.example.smartwaste_admin.domain.usecases.editorialusecase.AddEdiroailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class EditorialViewModel @Inject constructor(
    private val addEdiroailUseCase: AddEdiroailUseCase
) : ViewModel(){

    private val _addEditorialState= MutableStateFlow(CommonEditorialState<String>())
    val addEditorialState= _addEditorialState.asStateFlow()

    fun addEditorial(ediorialModel: EdiorialModel){
        viewModelScope.launch(Dispatchers.IO) {

            _addEditorialState.value= CommonEditorialState(isLoading = true)
            val result=addEdiroailUseCase.addEditorial(ediorialModel)

            when(result){
                is ResultState.Error -> {
                    _addEditorialState.value= CommonEditorialState(error = result.error)
                }
                is ResultState.Success -> {
                    _addEditorialState.value= CommonEditorialState(success = result.data)
                }
                is ResultState.Loading -> {

                }
            }
        }
    }
}


data class CommonEditorialState<T>(
    val isLoading : Boolean=false,
    val success:  T?=null,
    val error : String=""
)