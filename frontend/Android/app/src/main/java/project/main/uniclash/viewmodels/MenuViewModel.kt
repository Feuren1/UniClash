package project.main.uniclash.viewmodels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import project.main.uniclash.dataManagers.PermissionManager
import project.main.uniclash.datatypes.Student
import project.main.uniclash.datatypes.StudentRegisterRequest
import project.main.uniclash.datatypes.User
import project.main.uniclash.retrofit.UserService
import project.main.uniclash.retrofit.enqueue
import project.main.uniclash.dataManagers.UserDataManager
import project.main.uniclash.retrofit.ArenaService
import project.main.uniclash.retrofit.CritterService
import project.main.uniclash.retrofit.StudentHubService
import project.main.uniclash.retrofit.StudentService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MenuViewModel () : ViewModel() {
    private val permissionManager: PermissionManager by lazy {
        PermissionManager(Application())
    }

    fun returnPermission():Boolean {
        var permission = false
        viewModelScope.launch {
           permission =  permissionManager.getPublishLocationPermission()!!
        }
        return permission
    }

    fun setPermission() {
        viewModelScope.launch {
            permissionManager.storePublishLocationPermission(!permissionManager.getPublishLocationPermission()!!)
        }
    }

    companion object {
        fun provideFactory(
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return MenuViewModel(
                    ) as T
                }
            }
    }
}