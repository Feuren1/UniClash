package project.main.uniclash.datatypes

data class StudentRegisterRequest(
    val level: Int,
    val lat: Int,
    val lon: Int,
    val credits: Int,
    val expToNextLevel: Int,
    val placedBuildings: Int,
    val userId: String,
)
