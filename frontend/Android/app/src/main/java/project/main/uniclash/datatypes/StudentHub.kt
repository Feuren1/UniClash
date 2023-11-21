package project.main.uniclash.datatypes

data class StudentHub(
    val name: String,
    val description: String,
    val lat: Double,
    val lon: Double
){
    override fun toString(): String {
        return ("Name: $name, Description: $description, Lat: $lat, Lon $lon")
    }
}