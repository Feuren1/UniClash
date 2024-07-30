package project.main.uniclash.viewmodels
import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import project.main.uniclash.datatypes.ItemUsable
import project.main.uniclash.retrofit.InventoryService
import project.main.uniclash.retrofit.enqueue
import project.main.uniclash.dataManagers.UserDataManager

sealed interface ItemUsablesUIState {
    data class HasEntries(
        val itemUsables: List<ItemUsable>,
        val isLoading: Boolean,
    ) : ItemUsablesUIState
}

class InventoryViewModel(
    private val inventoryService: InventoryService,
    private val application: Application,
) : ViewModel(){

    private val userDataManager : UserDataManager by lazy {
        UserDataManager(application)
    }

    val itemUsables = MutableStateFlow(
        ItemUsablesUIState.HasEntries(
            emptyList(),
            isLoading = false
        )
    )

    @SuppressLint("MissingPermission")
    fun loadItemUsables() {
        viewModelScope.launch {
            itemUsables.update { it.copy(isLoading = true) }
            try {
                val response = inventoryService.getItemsFromStudent(userDataManager.getStudentId()!!).enqueue()
                if (response.isSuccessful) {
                    val itemUsable = response.body()!!
                    itemUsables.update {
                        it.copy(
                            itemUsables = itemUsable,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private val TAG = InventoryViewModel::class.java.simpleName

    companion object {
        fun provideFactory(
            inventoryService: InventoryService,
            application: Application,
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return InventoryViewModel(
                        inventoryService,
                        application,
                    ) as T
                }
            }
    }
}