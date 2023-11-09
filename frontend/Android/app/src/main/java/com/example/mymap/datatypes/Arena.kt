package com.example.mymap.datatypes

data class Arena(
    val name: String,
    val description: String,
    val lat: Long,
    val lon: Long
){
    override fun toString(): String {
        return ("$name stats: $name $description $lat $lon")
    }
}
