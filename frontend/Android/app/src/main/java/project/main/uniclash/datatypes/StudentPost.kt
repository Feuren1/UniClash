package project.main.uniclash.datatypes

data class StudentPost(
    var level: Int,
    var lat: Int,
    var lon: Int,
    var credits: Int,
    var expToNextLevel: Int,
    var userId: String
){

    override fun toString(): String {
        return "StudentCreate(level=$level, lat=$lat, lon=$lon, credits=$credits, expToNextLevel=$expToNextLevel, userId='$userId')"
    }
}
