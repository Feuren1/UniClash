package project.main.uniclash.datatypes

data class Student(
    var team: ArrayList<Critter>,
    var xp: Int
){
    override fun toString(): String {
        return "Student(team=$team, xp=$xp)"
    }
}
