package project.main.uniclash.viewmodels
import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import project.main.uniclash.datatypes.CritterUsable
import project.main.uniclash.datatypes.ItemUsable
import project.main.uniclash.retrofit.ArenaService
import project.main.uniclash.retrofit.InventoryService
import project.main.uniclash.retrofit.enqueue

sealed interface ItemUsablesUIState {
    data class HasEntries(
        val itemUsables: List<ItemUsable>,
        val isLoading: Boolean,
    ) : ItemUsablesUIState
}

class InventoryViewModel(
    private val inventoryService: InventoryService,
) : ViewModel(){

    val itemUsables = MutableStateFlow(
        ItemUsablesUIState.HasEntries(
            emptyList(),
            isLoading = false
        )
    )

    @SuppressLint("MissingPermission")
    fun loadItemUsables(id: Int) {
        viewModelScope.launch {
            itemUsables.update { it.copy(isLoading = true) }
            try {
                val response = inventoryService.getItemsFromStudent(id).enqueue()
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
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return InventoryViewModel(
                        inventoryService,
                    ) as T
                }
            }
    }
}