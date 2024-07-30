package project.main.uniclash.viewmodels

import android.content.Context
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import net.bytebuddy.agent.VirtualMachine.ForOpenJ9.Dispatcher
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.mockito.Mockito.`when`
import org.junit.Test
import project.main.uniclash.MapActivity
import project.main.uniclash.datatypes.Arena
import project.main.uniclash.retrofit.ArenaService
import project.main.uniclash.retrofit.CritterService
import project.main.uniclash.retrofit.StudentHubService
import project.main.uniclash.retrofit.enqueue
import retrofit2.Call
import retrofit2.Response
class MapMarkerViewModelTest {

    private val critterService: CritterService = mockk()
    private val studentHubService: StudentHubService = mockk()
    private val arenaService: ArenaService = mockk()
    private lateinit var context: Context
    private val mapMarkerListViewModel: MapMarkerListViewModel = MapMarkerListViewModel()

    private lateinit var viewModel: MapMarkerViewModel

    /*@Before
    fun setup() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        viewModel = MapMarkerViewModel(critterService, studentHubService, arenaService, context, mapMarkerListViewModel)
    }*/

    /*@After
    fun tearDown() {
        Dispatchers.resetMain()
    }*/

    @Test
    fun loadArenasTest() = runBlockingTest {
        println("start")
        val arena1 = Arena(id = 1, critterId = 1, description = "Mock Arena 1", lat = 0.0, lon = 0.0, name = "Arena 1", picture = "", studentId = 4)
        val arena2 = Arena(id = 2, critterId = 2, description = "Mock Arena 2", lat = 1.0, lon = 1.0, name = "Arena 2", picture = "", studentId = 5)

        val mockArenaList: List<Arena> = listOf(arena1, arena2)
        val mockArenaCall: Call<List<Arena>> = mockk()

        // Set up the behavior of mockArenaCall
        coEvery { mockArenaCall.enqueue() } answers {
            Response.success(mockArenaList)
        }

        // Set up the behavior of arenaService.getArenas() to return mockArenaCall
        coEvery { arenaService.getArenas() } returns mockArenaCall

        viewModel.loadArenas()

        val foundedArena: List<Arena?> = viewModel.arenas.value.arenas
        println("output")
        println(foundedArena.toString())
        assertTrue(foundedArena == mockArenaList)
    }

    @Test
    fun testTest(){
        assertTrue(1 ==1)
    }
    @Test
    suspend fun initMarkersArenaTest() {
        viewModel = MapMarkerViewModel(critterService, studentHubService, arenaService, context.applicationContext, mapMarkerListViewModel)

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

        var arenaMarker = viewModel.markersArena.value.makersArena

        viewModel.initMarkersArena()
        println(arenaMarker.toString())
        assertTrue(arenaMarker.isNotEmpty())
    }
}