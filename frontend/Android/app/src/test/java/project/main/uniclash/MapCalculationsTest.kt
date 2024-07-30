package project.main.uniclash

import junit.framework.TestCase.assertEquals
import org.junit.Test
import org.osmdroid.util.GeoPoint
import project.main.uniclash.map.MapCalculations

class MapCalculationsTest {
    var mapCalculations = MapCalculations()

    @Test
    fun calculateDirectionTest(){
        var startPoint = GeoPoint(0.0, 0.0)
        var endPoint = GeoPoint(1.0, 1.0)

        val result = mapCalculations.calculateDirection(startPoint, endPoint)

        startPoint = GeoPoint(0.0, 0.0)
        endPoint = GeoPoint(-1.0, -1.0)

        val result2 = mapCalculations.calculateDirection(startPoint, endPoint)

        startPoint = GeoPoint(0.0, 0.0)
        endPoint = GeoPoint(1.0, -1.0)

        val result3 = mapCalculations.calculateDirection(startPoint, endPoint)

        startPoint = GeoPoint(0.0, 0.0)
        endPoint = GeoPoint(-1.0, 1.0)

        val result4 = mapCalculations.calculateDirection(startPoint, endPoint)

        assertEquals(45.0f, result, 0.01f)
        assertEquals(225.0f, result2, 0.01f)
        assertEquals(135.0f, result3, 0.01f)
        assertEquals(315.0f, result4, 0.01f)
    }

    @Test
    fun distanceTest(){
        var distance = mapCalculations.distance(51.495745,6.294864,51.353111,6.155168)
        var rightDistance = 18580.0
        assertEquals(rightDistance,distance,15.0)
    }
}