package project.main.uniclash.datatypes

data class CreateStudentRequest(
    var id: Int,
    var level: Int,
    var lat: Int,
    var lon: Int,
    var credits: Int,
    var expToNextLevel: Int,
    var userId: String,
){

}
