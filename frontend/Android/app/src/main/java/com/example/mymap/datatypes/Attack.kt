package com.example.mymap.datatypes

import androidx.lifecycle.ViewModel
import com.example.mymap.retrofit.ArenaService
import com.example.mymap.retrofit.CritterService

data class Attack(
    val id: Int,
    val name: String,
    val strength: Int
)


