package project.main.uniclash.viewmodels

import android.content.Context
import kotlinx.coroutines.flow.update
import org.junit.Assert
import org.junit.Test
import project.main.uniclash.datatypes.Arena
import project.main.uniclash.retrofit.ArenaService
import project.main.uniclash.retrofit.CritterService
import project.main.uniclash.retrofit.StudentHubService

class MapMarkerViewModelLogicTest {
    private lateinit var context: Context

    private val critterService = CritterService
    private val studentHubService = StudentHubService
    private val arenaService = ArenaService
    private val mapMarkerListViewModel: MapMarkerListViewModel = MapMarkerListViewModel()

    private lateinit var viewModel: MapMarkerViewModel
    @Test
    fun testTest(){
        Assert.assertTrue(1 == 1)
    }

    @Test
    suspend fun initMarkersArenaTest() {
        context = context.applicationContext
        viewModel = MapMarkerViewModel(critterService.getInstance(context), studentHubService.getInstance(context), arenaService.getInstance(context), context.applicationContext, mapMarkerListViewModel)

        val arena1 = Arena(
            id = 1,
            critterId = 1,
            description = "Mock Arena 1",
            lat = 0.0,
            lon = 0.0,
            name = "Arena 1",
            picture = "",
            studentId = 4
        )
        val arena2 = Arena(
            id = 2,
            critterId = 2,
            description = "Mock Arena 2",
            lat = 1.0,
            lon = 1.0,
            name = "Arena 2",
            picture = "",
            studentId = 5
        )
        println("start")

        var mockArenaList: List<Arena> = listOf(arena1, arena2)

        viewModel.arenas.update {
            it.copy(
                arenas = mockArenaList,
            )
        }

        viewModel.initMarkersArena()

        var arenaMarker = viewModel.markersArena.value.makersArena

        println(arenaMarker.toString())
        Assert.assertTrue(arenaMarker.isNotEmpty())
    }
}