package project.main.uniclash.datatypes

data class Student(
    var id: Int,
    var level: Int,
    var lat: String,
    var lon: String,
    var credits: Int,
    var expToNextLevel: Int,
    var userId: String,
    var placedBuildings : Int,
){
    override fun toString(): String {
        return "Student(id=$id, level=$level, lat=$lat, lon=$lon, credits=$credits, expToNextLevel=$expToNextLevel, userId='$userId', placedBuildings=$placedBuildings)"
    }
}
