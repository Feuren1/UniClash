package project.main.uniclash.datatypes

data class Student(
    var id: Int,
    var level: Int,
    var lat: Int,
    var lon: Int,
    var credits: Int,
    var expToNextLevel: Int,
    var userId: String
){
    override fun toString(): String {
        return "Student(id=$id, level=$level, lat=$lat, lon=$lon, credits=$credits, expToNextLevel=$expToNextLevel, userId='$userId')"
    }
}
