package project.main.uniclash.datatypes

data class CritterTemplate(
    val id: Int,
    val name: String,
    val baseHealth: Int,
    val baseAttack: Int,
    val baseDefence: Int,
    val baseSpeed: Int,
    val evolesAt: Int,
    val evolvesIntoTemplateId: Int,
){
    override fun toString(): String {
        return "CritterTemplate(id=$id, name='$name', baseHealth=$baseHealth, baseAttack=$baseAttack, baseDefence=$baseDefence, baseSpeed=$baseSpeed, evolesAt=$evolesAt, evolvesIntoTemplateId=$evolvesIntoTemplateId)"
    }

}
