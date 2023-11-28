package project.main.uniclash.viewmodels

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import project.main.uniclash.retrofit.CritterService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import project.main.uniclash.datatypes.CritterTemplate
import project.main.uniclash.retrofit.enqueue
sealed interface CritterTemplatesUIState {
    data class HasEntries(
        val critterTemplates: List<CritterTemplate?>,
        val isLoading: Boolean,
    ) : CritterTemplatesUIState
}

sealed interface CritterTemplatesOrderedUIState {
    data class HasEntries(
        val critterTemplates: List<List<CritterTemplate>>,
    ) : CritterTemplatesOrderedUIState
}

class CritterDexViewModel(
    private val critterService: CritterService,
) : ViewModel() {
    //TAG for logging
    private val TAG = UniClashViewModel::class.java.simpleName

    val critterTemplates = MutableStateFlow(
        CritterTemplatesUIState.HasEntries(
            emptyList(),
            isLoading = false
        )
    )

    val critterTemplatesOrdered = MutableStateFlow(
        CritterTemplatesOrderedUIState.HasEntries(
            emptyList(),
        )
    )

    init {
        viewModelScope.launch {
            Log.d(TAG, "Fetching initial critters data: ")

            critterTemplates.collect {
                loadCritterTemplates()
            }

            // Observe changes in critterTemplatesOrdered and trigger reload
            critterTemplatesOrdered.collect {
                sortCritterTemplates()
            }
        }
    }
    @SuppressLint("MissingPermission")
    fun loadCritterTemplates() {
        viewModelScope.launch {
            critterTemplates.update { it.copy(isLoading = true) }
            try {
                val response = critterService.getCrittersTemplates().enqueue()
                Log.d(TAG, "loadCrittersTemplates: $response")
                if (response.isSuccessful) {
                    Log.d(TAG, "loadCrittersTemplates: success")
                    val crittersTemplates = response.body()!!
                    Log.d(TAG, "loadCrittersTemplates: $crittersTemplates")
                    critterTemplates.update {
                        it.copy(
                            critterTemplates = crittersTemplates,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun sortCritterTemplates(){
        viewModelScope.launch {
            val templatesOrdered = mutableListOf<ArrayList<CritterTemplate>>()


            val templates = critterTemplates.value.critterTemplates
            if (templates.isNotEmpty()) {

                println("${templates.size} hallohallohallohallohallohallo")

                for (i in 0 until templates.size) {
                    val currentTemplate = templates[i]
                    val template: ArrayList<CritterTemplate> = ArrayList()
                    templates[i]?.let { template.add(it) }
                    templatesOrdered.add(template)
                }

                critterTemplatesOrdered.update {
                    it.copy(critterTemplates = templatesOrdered)
                }
            }
        }
    }


    companion object {
        fun provideFactory(
            critterService: CritterService,
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return CritterDexViewModel(
                        critterService
                    ) as T
                }
            }
    }
}