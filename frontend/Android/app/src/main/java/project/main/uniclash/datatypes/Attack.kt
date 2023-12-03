package project.main.uniclash.datatypes

data class Attack(
    val id: Int,
    val name: String,
    val strength: Int,
    val attackType: AttackType,
)
