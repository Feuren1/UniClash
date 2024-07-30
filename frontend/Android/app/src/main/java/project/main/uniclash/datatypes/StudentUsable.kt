package project.main.uniclash.datatypes

import java.io.Serializable

class StudentUsable(student: Student) : Serializable {
    var student: Student = student
    var team : List<Critter>? = null
}
