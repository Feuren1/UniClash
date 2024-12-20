package project.main.uniclash.datatypes

data class Item(
    val quantity: Int,
    val itemTemplateId: Int,
    val studentId: Int
) {
    override fun toString(): String {
        return "ItemFromStudent(quantity=$quantity, itemTemplateId=$itemTemplateId, studentId=$studentId)"
    }
}
