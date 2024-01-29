import {injectable, service} from '@loopback/core';
import {repository} from "@loopback/repository";
import {Attack, Critter, CritterUsable, Student, WildencounterInformation} from "../models";
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
    const day: number = currentTime.getDay();
    const currentDates : WildencounterInformation[] = await this.wildEncounterInformationRepository.find()
    const currentDate : WildencounterInformation = currentDates[0]


    if(parseInt(currentDate.date)<day){
      await this.wildEncounterInformationRepository.delete(currentDate)
      currentDate.date = day.toString()
      this.refreshWildEncounterStats()
      await this.wildEncounterInformationRepository.create(currentDate)
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
}

