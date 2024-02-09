package project.main.uniclash.datatypes

data class Attack(
    val id: Int,
    val name: String,
    val strength: Int,
    val attackType: AttackType,
    val typeId: String
){

    constructor(id: Int, name: String, strength: Int, attackTypeString: String, type : String) :
            this(id, name, strength, AttackType.valueOf(attackTypeString), type)

    override fun toString(): String {
        return "Attack(id=$id- name='$name'- strength=$strength- attackType=$attackType)"
    }

    companion object {
        fun fromString(stringRepresentation: String): Attack {
            val parts = stringRepresentation.split("- ")
            println(stringRepresentation)

            val id = parts[0].substringAfter('=').toInt()
            val name = parts[1].substringAfter('=').removeSuffix("'")
            val strength = parts[2].substringAfter('=').toInt()
            val attackTypeString = parts[3].substringAfter('=')

            return Attack(id, name, strength, AttackType.valueOf(attackTypeString), "NORMAL")
        }
    }
}
