package project.main.uniclash.datatypes

data class Arena(
    val id : Int,
    val name : String,
    val description : String,
    val lat : Double,
    val lon : Double,
    var critterId: Int,
    var studentId: Int,
    val picture : String
){
    override fun toString(): String {
        return "Arena(id=$id, name='$name', description='$description', lat=$lat, lon=$lon, critterId=$critterId, studentId=$studentId, picture='$picture')"
    }
}
