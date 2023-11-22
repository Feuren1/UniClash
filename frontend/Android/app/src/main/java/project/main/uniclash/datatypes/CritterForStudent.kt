package project.main.uniclash.datatypes

data class CritterForStudent(
    val level: Int,
    val expToNextLevel : Int,
    val nature : String,
    val critterTemplateId : Int,
    val studentId : Int,
)
