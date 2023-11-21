package project.main.uniclash.battle

import project.main.uniclash.datatypes.Attack
import project.main.uniclash.datatypes.CritterUsable

class BattleLogicView(private val playerCritter: CritterUsable?, private val cpuCritter: CritterUsable?) {

    private val damageCalculator = DamageCalculator()
     var playerTurn = false
     var playerWinner = false
    private var cpuWinner = false
    private var roundCounter = 0

    // Max rounds for the battle (you can adjust this)
    private val maxRounds = 10


    fun init(){
        if(playerCritter!!.spd>cpuCritter!!.spd){
            playerTurn = true
        }
        if (playerCritter!!.spd==cpuCritter!!.spd){

        }
        else {
            playerTurn = false
        }
    }
    fun attack(attack: Attack): Int {
        val attacker = if (playerTurn) playerCritter else cpuCritter
        val defender = if (playerTurn) cpuCritter else playerCritter

        val damage = damageCalculator.calculateDamage(attack, attacker, defender)
        defender!!.reduceHealth(damage)
        checkWinner()
        endRound()
        return damage
    }

    fun checkWinner() {
        if (playerCritter!!.hp <= 0) {
            playerWinner = true
        } else if (cpuCritter!!.hp <= 0) {
            cpuWinner = true
        }
    }

    fun switchTurns() {
        playerTurn = !playerTurn
    }

    fun letCpuAttack(){

    }

    fun endRound() {
        roundCounter++
        if (roundCounter >= maxRounds) {
            // Handle a draw if no winner after max rounds
        }
        switchTurns()
    }

    fun isBattleOver(): Boolean {
        return playerWinner || cpuWinner || roundCounter >= maxRounds
    }

    fun getBattleResult(): BattleResult {
        if (playerWinner) {
            return BattleResult.PLAYER_WINS
        } else if (cpuWinner) {
            return BattleResult.CPU_WINS
        } else {

        }
        return BattleResult.NOTOVER
    }
}
