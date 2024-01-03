package project.main.uniclash


import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import project.main.uniclash.datatypes.Attack
import project.main.uniclash.datatypes.Critter
import project.main.uniclash.retrofit.CritterService


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.mymap", appContext.packageName)
    }
    /*
    @Test
    fun doAction_doesSomething(){
        /* Given */
        val mockedList: List<*> = mock(MutableList::class.java)

        val mock = mock(CritterService::class.java)
        var attack = Attack(12,"12",12, "DAMAGE_DEALER")
        val critter = Critter(10,20,20,20,attack,attack,attack,attack,"Test",1)
        `when`(mock.getCritters(2)).thenReturn(critter)
        val classUnderTest = ClassUnderTest(mock)

        /* When */
        classUnderTest.doAction()

        /* Then */
        verify(mock).doSomething(any())
    }
    @Test
    fun doantyhi(){
        val mockedList = mock(MutableList::class.java)
// or even simpler with Mockito 4.10.0+
// List mockedList = mock();

// using mock object - it does not throw any "unexpected interaction" exception
// or even simpler with Mockito 4.10.0+
// List mockedList = mock();

// using mock object - it does not throw any "unexpected interaction" exception
        mockedList.add("one")
        mockedList.clear()

// selective, explicit, highly readable verification

// selective, explicit, highly readable verification
        verify(mockedList).add("one")
        verify(mockedList).clear()
    }
     */
}