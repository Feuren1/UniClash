package project.main.uniclash.datatypes

import com.google.gson.annotations.SerializedName

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
