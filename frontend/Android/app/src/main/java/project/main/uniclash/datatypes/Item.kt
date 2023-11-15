package project.main.uniclash.datatypes

data class Item(
    val name: String,
    val cost: Int
){
    override fun toString(): String {
        return ("Item name: $name, cost: $cost")
    }
}