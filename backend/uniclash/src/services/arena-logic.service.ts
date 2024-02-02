import {createProxyWithInterceptors, injectable, service} from '@loopback/core';
import {repository} from "@loopback/repository";
import {
  ArenaRepository,
  CritterInFightRepository,
  CritterRepository,
  CritterTemplateRepository,
  StudentRepository,
  UserRepository
} from "../repositories";
import {LevelCalcStudentService} from "./levelCalc-student.service";
import {CritterStatsService} from "./critter-stats.service";
import {LevelCalcCritterService} from "./levelCalc-critter.service";

@injectable()
export class ArenaLogicService {
  constructor(
    @repository(CritterTemplateRepository) protected critterTemplateRepository: CritterTemplateRepository,
    @repository(CritterRepository) protected critterRepository: CritterRepository,
    @repository(ArenaRepository) protected arenaRepository : ArenaRepository,
    @repository(CritterInFightRepository) protected critterInFightRepository : CritterInFightRepository,
    @repository(StudentRepository) protected studentRepository: StudentRepository,
    @service(LevelCalcStudentService) protected levelCalcStudentService : LevelCalcStudentService,
    @service(LevelCalcCritterService) protected levelCalcCritterService : LevelCalcCritterService,
    @service(CritterStatsService) protected critterStatsService : CritterStatsService,
    @repository(UserRepository) protected userRepository: UserRepository,
  ) { }

  async checkCritterInArenaIsAvailable(arenaId : number){
    const arena = await this.arenaRepository.findById(arenaId)
    const critters = await this.critterRepository.find()
    let canFind = false

    for(const critter of critters){
      if(critter.id == arena.critterId) canFind = true
    }
    if(!canFind){
      arena.critterId = 194
      arena.studentId = 1
      await this.arenaRepository.update(arena)
    }
  }

  async checkIfArenaOwnerHasTimeout(arenaId : number){
    const arena = await this.arenaRepository.findById(arenaId)
    const currentTime: Date = new Date();
    const time = currentTime.getMinutes()
    if(arena.invasionTime == null) {
      arena.invasionTime = time
      await this.arenaRepository.update(arena)
    }
    if(arena.critterId == 0 && arena.studentId != 0){
      if(arena.invasionTime != time && arena.invasionTime+1 != time && arena.invasionTime+2 != time){
        arena.critterId = 194
        arena.studentId = 1
        await this.arenaRepository.update(arena)
      }
    }
  }

  async setInvasionTime(arenaId : number){
    const arena = await this.arenaRepository.findById(arenaId)
    const currentTime: Date = new Date();
    arena.invasionTime = currentTime.getMinutes()
    await this.arenaRepository.update(arena)
  }
}

