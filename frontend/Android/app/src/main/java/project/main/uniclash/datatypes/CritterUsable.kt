package project.main.uniclash.datatypes

import java.sql.SQLOutput

data class CritterUsable(
    val level: Int,
    val name: String,
    var hp: Int,
    val atk: Int,
    val def: Int,
    val spd: Int,
    val attacks: List<Attack>,
    val critterId: Int,
    val critterTemplateId: Int,
    val expToNextLevel : Int,
    val type : String
){
    fun reduceHealth(damage: Int) {
    this.hp.minus(damage)
	}
    override fun toString(): String {
        return "CritterUsable(level=$level, name='$name', hp=$hp, atk=$atk, def=$def, spd=$spd, critterId=$critterId, critterTemplateId=$critterTemplateId, type=$type attacks=${attacks})"
    }
    companion object {
        fun fromString(stringRepresentation: String): CritterUsable {
            val parts = stringRepresentation.split(", ")

            val level = parts[0].substringAfter('=').toInt()
            val name = parts[1].substringAfter("='").removeSuffix("'")
            val hp = parts[2].substringAfter('=').toInt()
            val atk = parts[3].substringAfter('=').toInt()
            val def = parts[4].substringAfter('=').toInt()
            val spd = parts[5].substringAfter('=').toInt()
            val critterId = parts[6].substringAfter('=').toInt()
            val critterTemplateId = parts[7].substringAfter('=').toInt()
            val type = parts[8].substringAfter("type=").removeSuffix("'").substringBefore(" attacks")
            //val rest = parts[9].substringAfter('=').removeSuffix("'")
            println("$type e")
            // Parse attacks list based on your Attack class structure
            var attacks: List<Attack> = List(4) { index ->
                when (index) {
                    0 -> Attack(1, "Tackle", 45, AttackType.DAMAGE_DEALER, "NORMAL")
                    1 -> Attack(1, "Tackle", 45, AttackType.DAMAGE_DEALER,"NORMAL")
                    2 -> Attack(1, "Tackle", 45, AttackType.DAMAGE_DEALER,"NORMAL")
                    3 -> Attack(1, "Tackle", 45, AttackType.DAMAGE_DEALER,"NORMAL")
                    else -> throw IndexOutOfBoundsException("Invalid index: $index")
                } //sample data
            }

            //TODO replace sample data with real attacks
            /*var attacksString = parts[8].substringAfter('(')
            //val attacks = if (attacksString.isNotEmpty()) {
            println("$attacksString attacken")
            attacksString.split("], ").map { Attack.fromString(it) }

            attacksString = parts[9].substringAfter('(')
            //attacks = if (attacksString.isNotEmpty()) {
            println("$attacksString attacken")
            attacksString.split("], ").map { Attack.fromString(it) }

            attacksString = parts[10].substringAfter('(')
            //attacks = if (attacksString.isNotEmpty()) {
            println("$attacksString attacken")
            attacksString.split("], ").map { Attack.fromString(it) }*/
            //} else {
            //     emptyList()
            //}

            return CritterUsable(
                level,
                name,
                hp,
                atk,
                def,
                spd,
                attacks,
                critterId,
                critterTemplateId,
                0,
                type
            )
        }
    }
}
