package project.main.uniclash.onlineFight

interface FightState {

    fun makeDamage()
    fun exit()
    fun displayState():String
}