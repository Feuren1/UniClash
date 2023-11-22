package project.main.uniclash.datatypes

data class Item(
    val name: String,
    val quantity: Double,
    val itemTemplateId: Int,
){

    override fun toString(): String {
        return "Item(name='$name', quantity=$quantity, itemTemplateId=$itemTemplateId)"
    }
}
