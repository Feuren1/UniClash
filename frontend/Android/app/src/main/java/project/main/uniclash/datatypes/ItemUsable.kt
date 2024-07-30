package project.main.uniclash.datatypes

data class ItemUsable(
    val id: Int,
    val quantity: Double,
    val itemTemplateId: Int,
    val name : String,
    val cost : Int,
    val studentId : Int,
){
}
