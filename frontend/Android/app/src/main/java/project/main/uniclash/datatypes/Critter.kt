package project.main.uniclash.datatypes

data class Critter(
    var baseHealth: Int,
    var baseAttack: Int,
    var baseDefend: Int,
    var baseSpeed: Int,
    val attack1: Attack?,
    val attack2: Attack?,
    val attack3: Attack?,
    val attack4: Attack?,
    val name: String

) {
    override fun toString(): String {
        return ("$name stats: $baseHealth $baseAttack $baseDefend $baseSpeed")
    }

    fun getAttacks(): List<Attack> {
        val attackList = mutableListOf<Attack>()

        attack1?.let { attackList.add(it) }
        attack2?.let { attackList.add(it) }
        attack3?.let { attackList.add(it) }
        attack4?.let { attackList.add(it) }

        return attackList
    }

    fun reduceHealth(amount: Int){
        baseHealth.minus(amount)
    }

    fun increaseHealth(amount: Int){
        baseHealth.plus(amount)
    }

}
