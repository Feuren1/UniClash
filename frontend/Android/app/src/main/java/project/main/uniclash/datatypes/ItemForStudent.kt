package project.main.uniclash.datatypes

data class ItemForStudent(
    val quantity: Int,
    val itemTemplateId : Int,
    val studentId : Int
) {
    override fun toString(): String {
        return "ItemForStudent(quantity=$quantity, itemTemplateId=$itemTemplateId, studentId=$studentId)"
    }
}
