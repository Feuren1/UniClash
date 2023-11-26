package project.main.uniclash.datatypes

data class User(
    val name: String,
    val id: String,
    val student: Student
){
    override fun toString(): String {
        return "User(name='$name', id='$id', student=$student)"
    }
}
