package com.example.mymap.datatypes

import android.graphics.drawable.Drawable
import android.content.Context
import androidx.core.content.ContextCompat
import com.example.mymap.R

enum class CritterPic(private val drawableResId: Int) {
    PRC2DUCK(R.drawable.prc2duck),
    KNIFEDUCK(R.drawable.knifeduck),
    DEMOMUSK(R.drawable.demomusk),
    MUSK(R.drawable.musk),
    MOCKITO(R.drawable.mockitodrink),
    QUIZIZZDRAGON(R.drawable.quizizzdragon),
    FONTYS(R.drawable.fontys),
    LinuyPINGIUN(R.drawable.linuxpingiun),
    KNIFETURTLE(R.drawable.knifeturtle),
    COOLDUCK(R.drawable.coolduck),
    BORZOI(R.drawable.borzoi),
    PIKATCHU(R.drawable.pikatchu);

    fun getDrawable(): Int {
        return drawableResId
    }
}