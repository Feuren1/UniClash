package project.main.uniclash.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import project.main.uniclash.retrofit.enqueue
import project.main.uniclash.dataManagers.UserDataManager
import project.main.uniclash.datatypes.Arena
import project.main.uniclash.datatypes.Critter
import project.main.uniclash.datatypes.MarkerArena
import project.main.uniclash.datatypes.MarkerStudent
import project.main.uniclash.datatypes.NewArena
import project.main.uniclash.datatypes.OnlineFight
import project.main.uniclash.datatypes.OnlineFightInformation
import project.main.uniclash.datatypes.SelectedMarker
import project.main.uniclash.retrofit.OnlineFightService

sealed interface PostFightUIState {
    data class HasEntries(
        val isLoading: Boolean,
    ) : PostFightUIState
}

class CreateOnlineFightViewModel(
    private val onlineFightService: OnlineFightService,
    private val application: Application
) : ViewModel() {
    //TAG for logging
    private val TAG = OnlineFightListViewModel::class.java.simpleName
    private val userDataManager: UserDataManager by lazy {
        UserDataManager(application)
    }

    private val markerData = SelectedMarker.SELECTEDMARKER.takeMarker()
    private val studentMarker = if(markerData is MarkerStudent){markerData} else {null}

    private fun getSelectedStudent(): MarkerStudent?{
        return studentMarker
    }

    val postFight = MutableStateFlow(
        PostFightUIState.HasEntries(
            isLoading = false
        )
    )

    @SuppressLint("MissingPermission")
    fun createOnlineFight2() {
        viewModelScope.launch {
                try {
                    val response = onlineFightService.createFight(userDataManager.getStudentId()!!,getSelectedStudent()!!.student!!.id)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
    }

    fun createOnlineFight(){
        viewModelScope.launch {
            postFight.update { it.copy(isLoading = true) }
            try {
                val response = onlineFightService.createFight(userDataManager.getStudentId()!!,getSelectedStudent()!!.student!!.id).enqueue()
                Log.d(ContentValues.TAG, "createFight: $response")
                if (response.isSuccessful) {
                    Log.d(ContentValues.TAG, "Success: ${response.body()}")
                    response.body()?.let {
                        postFight.update { state ->
                            state.copy(isLoading = false)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    companion object {
        fun provideFactory(
            onlineFightService: OnlineFightService,
            application: Application,
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return CreateOnlineFightViewModel(
                        onlineFightService,
                        application
                    ) as T
                }
            }
    }
}