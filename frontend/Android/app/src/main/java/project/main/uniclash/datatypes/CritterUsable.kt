package project.main.uniclash.datatypes

data class CritterUsable(
    val level: Int,
    val name: String,
    val hp: Int,
    val atk: Int,
    val def: Int,
    val spd: Int,
    val attacks: List<Attack>
){
    fun reduceHealth(damage: Int) {
    this.hp.minus(damage)
	}
    override fun toString(): String {
        return "CritterUsable(level=$level, name='$name', hp=$hp, atk=$atk, def=$def, spd=$spd, attacks=$attacks)"
    }
}
