class Battle {
    trainerCritter: typeof Critter;
    enemyCritter: typeof Critter;
    //gui gets db info
    //gui calls this class for calc
    constructor(critter: typeof Critter, enemyCritter: typeof Critter) {
        this.trainerCritter = critter;
        this.enemyCritter = enemyCritter;
    }


}
