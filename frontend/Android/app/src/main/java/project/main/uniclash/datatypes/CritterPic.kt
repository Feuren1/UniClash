package project.main.uniclash.datatypes

import project.main.uniclash.R

enum class CritterPic(private val drawableResId: Int) {
    PRC2DUCK(R.drawable.prc2duck),
    PRC2DUCKM(R.drawable.prc2duckm),
    KNIFEDUCK(R.drawable.knifeduck),
    KNIFEDUCKM(R.drawable.knifeduckm),
    DEMOMUSK(R.drawable.demomusk),
    DEMOMUSKM(R.drawable.demomuskm),
    MUSK(R.drawable.musk),
    MUSKM(R.drawable.muskm),
    MOCKITO(R.drawable.mockitodrink),
    MOCKITOM(R.drawable.mockitodrinkm),
    QUIZIZZDRAGON(R.drawable.quizizzdragon),
    QUIZIZZDRAGONM(R.drawable.quizizzdragonm),
    FONTYS(R.drawable.fontys),
    FONTYSM(R.drawable.fontysm),
    LINUXPINGIUN(R.drawable.linuxpingiun),
    LINUXPINGIUNM(R.drawable.linuxpingiunm),
    KNIFETURTLE(R.drawable.knifeturtle),
    KNIFETURTLEM(R.drawable.knifeturtlem),
    COOLDUCK(R.drawable.coolduck),
    COOLDUCKM(R.drawable.coolduckm),
    BORZOI(R.drawable.borzoi),
    BORZOIM(R.drawable.borzoim),
    PIKATCHU(R.drawable.pikatchu),
    PIKATCHUM(R.drawable.pikatchum);

    fun getDrawable(): Int {
        return drawableResId
    }
}