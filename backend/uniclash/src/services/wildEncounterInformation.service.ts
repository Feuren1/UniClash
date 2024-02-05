import {injectable, service} from '@loopback/core';
import {repository} from "@loopback/repository";
import {
  Attack,
  AttackRelations,
  Critter,
  CritterAttack,
  CritterUsable,
  Student,
  WildencounterInformation
} from "../models";
import {
  AttackRepository,
  CritterAttackRepository,
  CritterRepository,
  StudentRepository,
  WildencounterInformationRepository
} from "../repositories";
import {CritterStatsService} from "./critter-stats.service";
import {LevelCalcStudentService} from "./levelCalc-student.service";
import { authenticate } from '../decorators';
import {BlockList} from "net";


@injectable()
export class WildEncounterInformationService {
  constructor(
    @repository(CritterRepository) protected critterRepository: CritterRepository,
    @repository(AttackRepository) protected attackRepository: AttackRepository,
    @repository(CritterAttackRepository) protected critterAttackRepository: CritterAttackRepository,
    @repository(WildencounterInformationRepository) protected wildEncounterInformationRepository: WildencounterInformationRepository,
  ) { }
  @authenticate('jwt')
  async checkOfWildEncounterShouldReload(): Promise<void> {
    const currentTime: Date = new Date();
    const day: number = currentTime.getTime();
    const currentDates : WildencounterInformation[] = await this.wildEncounterInformationRepository.find()
    const currentDate : WildencounterInformation = currentDates[0]

    if(day - parseInt(currentDate.date) > 21600000){
      await this.wildEncounterInformationRepository.delete(currentDate)
      currentDate.date = day.toString()
      await this.wildEncounterInformationRepository.create(currentDate)
      await this.refreshWildEncounterStats()
      await this.refreshWildEncounterAttacks()
    }
  }

  @authenticate('jwt')
  async refreshWildEncounterStats():Promise<void>{
    const wildEncounters : Critter[] = await this.critterRepository.find()

    for(const wildEncounter of wildEncounters){
        if(wildEncounter.studentId == 1){
          wildEncounter.level = Math.floor(Math.random() * 15) + 1
          await this.critterRepository.update(wildEncounter);
        }
        if(wildEncounter.studentId == 2){
          wildEncounter.level = Math.floor(Math.random() * 25) + 1
          await this.critterRepository.update(wildEncounter);
        }
    }
  }

  async refreshWildEncounterAttacks():Promise<void>{
    const critterIDs : number[] = []
    const newAttackRelationIDs : number[] = []
    const allCritter : Critter[] = await this.critterRepository.find()
    const allAttacks : Attack[] = await this.attackRepository.find()
    const allAttackRelations : CritterAttack[] = await this.critterAttackRepository.find()

    for(const critter of allCritter)if(critter.studentId == 1 || critter.studentId == 2){ // @ts-ignore
      critterIDs.push(critter.id)
    }
    let randomAttack1: number = 1
    let randomAttack2: number = 1
    let randomAttack3: number = 1
    let randomAttack4: number = 1

    let allCritterIDs = critterIDs.length
    while(allCritterIDs > 0) {
      let reload = true
      while (reload) {
        reload = false
        randomAttack1 = Math.floor(Math.random() * allAttacks.length);
        randomAttack2 = Math.floor(Math.random() * allAttacks.length);
        if (randomAttack1 == randomAttack2) reload = true
        randomAttack3 = Math.floor(Math.random() * allAttacks.length);
        if (randomAttack1 == randomAttack3 || randomAttack2 == randomAttack3) reload = true
        randomAttack4 = Math.floor(Math.random() * allAttacks.length);
        if (randomAttack1 == randomAttack4 || randomAttack2 == randomAttack4 || randomAttack3 == randomAttack4) reload = true
        if (allAttacks[randomAttack1].attackType.toString() != "DAMAGE_DEALER" && allAttacks[randomAttack2].attackType.toString() != "DAMAGE_DEALER" && allAttacks[randomAttack3].attackType.toString() != "DAMAGE_DEALER" && allAttacks[randomAttack4].attackType.toString() != "DAMAGE_DEALER") reload = true
      }
      newAttackRelationIDs.push(randomAttack1,randomAttack2,randomAttack3,randomAttack4)
      allCritterIDs--
    }

    let index = 0
    for(const critterId of critterIDs){
      for(const attackRelation of allAttackRelations){
        if(critterId == attackRelation.critterId){
          console.log(critterId + " gets " + newAttackRelationIDs[index])
          // @ts-ignore
          if(allAttacks[newAttackRelationIDs[index]].id != null)attackRelation.attackId = allAttacks[newAttackRelationIDs[index]].id
          index++
        }
      }
    }


    for (const attackRelation of allAttackRelations) {
      let shouldUpload = false
      for(const critterId of critterIDs) if(critterId == attackRelation.critterId) shouldUpload = true
      if(shouldUpload)await this.critterAttackRepository.updateById(attackRelation.id, attackRelation);
    }
  }
}

