package project.main.uniclash.datatypes

data class Student(
    var team: ArrayList<Critter>,
    var xp: Int,
    var id: Int,
    var level: Int,
    var lat: Double,
    var lon: Double,
    var credits: Double,
    val expToNextLevel: Int,
    val userId: String,
){
    override fun toString(): String {
        return "Student(team=$team, xp=$xp, id=$id, level=$level, lat=$lat, lon=$lon, credits=$credits, expToNextLevel=$expToNextLevel, userId='$userId')"
    }
}
