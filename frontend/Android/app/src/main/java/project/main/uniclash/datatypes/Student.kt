package project.main.uniclash.datatypes

data class Student(
    var team: ArrayList<Critter>,
    var id: Int,
    var xp: Int,
    var level: Int,
    var lat: Int,
    var lon: Int,
    var credits: Int,
    var expToNextLevel: Int,
    var userId: String
){
    override fun toString(): String {
        return "Student(team=$team, id=$id, xp=$xp, level=$level, lat=$lat, lon=$lon, credits=$credits, expToNextLevel=$expToNextLevel, userId='$userId')"
    }
}
