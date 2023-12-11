package project.main.uniclash.viewmodels
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import project.main.uniclash.retrofit.ArenaService
import project.main.uniclash.retrofit.InventoryService
import project.main.uniclash.retrofit.enqueue



class InventoryViewModel(
    private val inventoryService: InventoryService,
) : ViewModel(){

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