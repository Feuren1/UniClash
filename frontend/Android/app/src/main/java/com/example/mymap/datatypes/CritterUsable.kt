package com.example.mymap.datatypes

data class CritterUsable(
    val level: Int,
    val name: String,
    val hp: Int,
    val atk: Int,
    val def: Int,
    val spd: Int,
    val attacks: List<Attack>
){

}
