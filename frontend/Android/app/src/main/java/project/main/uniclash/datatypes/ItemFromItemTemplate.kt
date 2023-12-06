package project.main.uniclash.datatypes

data class ItemFromItemTemplate(
    val id: Int,
    val quantity: Int,
    val itemTemplateId: Int,
    val studentId: Int
) {
    override fun toString(): String {
        return "ItemFromItemTemplate(id=$id, quantity=$quantity, itemTemplateId=$itemTemplateId, studentId=$studentId)"
    }
}
