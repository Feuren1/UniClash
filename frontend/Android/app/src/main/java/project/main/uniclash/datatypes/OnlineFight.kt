package project.main.uniclash.datatypes

data class OnlineFight(
    val fightId : Int,
    val studentId : Int,
    val critterId : Int,
    val state : String,
    val fightConnectionId : Int,
    val startTime: Int,
    val timer: Int,
){
}
