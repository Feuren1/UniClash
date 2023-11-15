package project.main.uniclash.datatypes

data class StudentHub(
    val name: String,
    val description: String,
    val lat: Long,
    val lon: Long
){
    override fun toString(): String {
        return ("Name: $name, Description: $description, Lat: $lat, Lon $lon")
    }
}