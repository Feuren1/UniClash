package com.example.mymap.datatypes

data class Critter(
   var hp: Int,
   var atk: Int,
   var def: Int,
   var spd: Int,
   val attack1: Attack?,
   val attack2: Attack?,
   val attack3: Attack?,
   val attack4: Attack?,
   val name: String

) {
    override fun toString(): String {
        return ("$name stats: $hp $atk $def $spd")
    }

    fun getAttacks(): List<Attack> {
        val attackList = mutableListOf<Attack>()

        attack1?.let { attackList.add(it) }
        attack2?.let { attackList.add(it) }
        attack3?.let { attackList.add(it) }
        attack4?.let { attackList.add(it) }

        return attackList
    }
}
