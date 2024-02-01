package project.main.uniclash.datatypes

enum class OnlineFightState(private var state : String) {
    YOURTURN("yourTurn"),
    ENEMYTURN("enemyTurn"),
    WAITING("waiting"),
    WINNER("winner"),
    LOSER("loser"),
    NOTFOUND("404");

    fun getState():String{
        return state
    }
}