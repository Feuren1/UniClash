package project.main.uniclash.datatypes

data class ItemTemplate(
    val id: Int,
    val name: String,
    val cost: Int
){
    override fun toString(): String {
        return "ItemTemplate(id=$id, name='$name', cost=$cost)"
    }
}