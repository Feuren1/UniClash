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
    PIKACHU(R.drawable.pikachu),
    PIKACHUM(R.drawable.pikachum),
    MATRYOSHKA(R.drawable.matryoshka),
    MATRYOSHKAM(R.drawable.matryoshkam),
    NUTCRACKER(R.drawable.nutcracker),
    NUTCRACKERM(R.drawable.nutcrackerm),
    EGGGIVINGWOOLMILKPIG(R.drawable.egggivingwoolmilk),
    EGGGIVINGWOOLMILKPIGM(R.drawable.egggivingwoolmilkm),
    MUTANTDUCK(R.drawable.mutantduck),
    MUTANTDUCKM(R.drawable.mutantduckm),
    STUDENTASSISTANCE(R.drawable.studentassistance),
    STUDENTASSISTANCEM(R.drawable.studentassistancem),
    CHARMANDER(R.drawable.charmander),
    CHARMANDERM(R.drawable.charmanderm),
    CHARIZARD(R.drawable.charizard),
    CHARIZARDM(R.drawable.charizardm),
    CHARMELON(R.drawable.charmeleon),
    CHARMELONM(R.drawable.charmeleonm),
    CROCODILEDUCK(R.drawable.crocodileduck),
    SNORLAX(R.drawable.snorlax),
    SNORLAXM(R.drawable.snorlax),
    CROCODILEDUCKM(R.drawable.crocodileduckm);

    fun getDrawable(): Int {
        return drawableResId
    }

    fun searchDrawable(searchTerm : String) : Int{
        if(searchTerm.equals("CHARMANDER")){
            return CHARMANDER.getDrawable()
        }
        if(searchTerm.equals("CHARIZARD")){
            return CHARIZARD.getDrawable()
        }
        if(searchTerm.equals("CHARMELEON")){
            return CHARMELON.getDrawable()
        }
        if(searchTerm.equals("PRC2DUCK")){
            return PRC2DUCK.getDrawable()
        }
        if(searchTerm.equals("KNIFEDUCK")){
            return KNIFEDUCK.getDrawable()
        }
        if(searchTerm.equals("DEMOMUSK")){
            return DEMOMUSK.getDrawable()
        }
        if(searchTerm.equals("MUSK")){
            return MUSK.getDrawable()
        }
        if(searchTerm.equals("MOCKITO")){
            return MOCKITO.getDrawable()
        }
        if(searchTerm.equals("QUIZIZZDRAGON")){
            return QUIZIZZDRAGON.getDrawable()
        }
        if(searchTerm.equals("FONTYS")){
            return FONTYS.getDrawable()
        }
        if(searchTerm.equals("LINUXPINGIUN")){
            return LINUXPINGIUN.getDrawable()
        }
        if(searchTerm.equals("KNIFETURTLE")){
            return KNIFETURTLE.getDrawable()
        }
        if(searchTerm.equals("COOLDUCK")){
            return COOLDUCK.getDrawable()
        }
        if(searchTerm.equals("BORZOI")){
            return BORZOI.getDrawable()
        }
        if(searchTerm.equals("PIKACHU")){
            return PIKACHU.getDrawable()
        }
        if(searchTerm.equals("EGGGIVINGWOOLMILKPIG")){
            return EGGGIVINGWOOLMILKPIG.getDrawable()
        }
        if(searchTerm.equals("NUTCRACKER")){
            return NUTCRACKER.getDrawable()
        }
        if(searchTerm.equals("MATRYOSHKA")){
            return MATRYOSHKA.getDrawable()
        }
        if(searchTerm.equals("MUTANTDUCK")){
            return MUTANTDUCK.getDrawable()
        }
        if(searchTerm.equals("CROCODILEDUCK")){
            return CROCODILEDUCK.getDrawable()
        }
        if(searchTerm.equals("STUDENTASSISTANCE")){
            return STUDENTASSISTANCE.getDrawable()
        }
        if(searchTerm.equals("SNORLAX")){
            return SNORLAX.getDrawable()
        }
        return R.drawable.hub
    }

    fun searchDrawableM(searchTerm : String) : Int{
        if(searchTerm.equals("CHARMANDERM")){
            return CHARMANDERM.getDrawable()
        }
        if(searchTerm.equals("CHARIZARDM")){
            return CHARIZARDM.getDrawable()
        }
        if(searchTerm.equals("CHARMELEONM")){
            return CHARMELONM.getDrawable()
        }
        if(searchTerm.equals("PRC2DUCKM")){
            return PRC2DUCKM.getDrawable()
        }
        if(searchTerm.equals("KNIFEDUCKM")){
            return KNIFEDUCKM.getDrawable()
        }
        if(searchTerm.equals("DEMOMUSKM")){
            return DEMOMUSKM.getDrawable()
        }
        if(searchTerm.equals("MUSKM")){
            return MUSKM.getDrawable()
        }
        if(searchTerm.equals("MOCKITOM")){
            return MOCKITOM.getDrawable()
        }
        if(searchTerm.equals("QUIZIZZDRAGONM")){
            return QUIZIZZDRAGONM.getDrawable()
        }
        if(searchTerm.equals("FONTYSM")){
            return FONTYSM.getDrawable()
        }
        if(searchTerm.equals("LINUXPINGIUNM")){
            return LINUXPINGIUNM.getDrawable()
        }
        if(searchTerm.equals("KNIFETURTLEM")){
            return KNIFETURTLEM.getDrawable()
        }
        if(searchTerm.equals("COOLDUCKM")){
            return COOLDUCKM.getDrawable()
        }
        if(searchTerm.equals("BORZOIM")){
            return BORZOIM.getDrawable()
        }
        if(searchTerm.equals("PIKACHUM")){
            return PIKACHUM.getDrawable()
        }
        if(searchTerm.equals("EGGGIVINGWOOLMILKPIGM")){
            return EGGGIVINGWOOLMILKPIGM.getDrawable()
        }
        if(searchTerm.equals("NUTCRACKERM")){
            return NUTCRACKERM.getDrawable()
        }
        if(searchTerm.equals("MATRYOSHKAM")){
            return MATRYOSHKAM.getDrawable()
        }
        if(searchTerm.equals("MUTANTDUCKM")){
            return MUTANTDUCKM.getDrawable()
        }
        if(searchTerm.equals("CROCODILEDUCKM")){
            return CROCODILEDUCKM.getDrawable()
        }
        if(searchTerm.equals("STUDENTASSISTANCEM")){
            return STUDENTASSISTANCEM.getDrawable()
        }
        if(searchTerm.equals("SNORLAXM")){
            return SNORLAXM.getDrawable()
        }
        return R.drawable.hub
    }
}