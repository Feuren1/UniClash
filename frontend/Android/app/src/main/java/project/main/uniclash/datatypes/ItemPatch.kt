package project.main.uniclash.datatypes

data class ItemPatch(
    val id: Int,
    val quantity: Int,
    val itemTemplateId: Int,
    val studentId: Int
){

    override fun toString(): String {
        return "Item(id=$id, quantity=$quantity, itemTemplateId=$itemTemplateId, studentId=$studentId)"
    }
}
