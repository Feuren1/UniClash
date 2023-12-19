package project.main.uniclash.datatypes

data class Attack(
    val id: Int,
    val name: String,
    val strength: Int,
    val attackType: AttackType,
){
    constructor(id: Int, name: String, strength: Int, attackTypeString: String) :
            this(id, name, strength, AttackType.valueOf(attackTypeString))
}
