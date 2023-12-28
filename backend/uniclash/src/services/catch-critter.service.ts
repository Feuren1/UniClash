import {injectable, service} from '@loopback/core';
import {repository} from "@loopback/repository";
import {Attack, Critter, CritterUsable, Student} from "../models";
import {AttackRepository, CritterAttackRepository, CritterRepository, StudentRepository} from "../repositories";
import {CritterStatsService} from "./critter-stats.service";
import {LevelCalcStudentService} from "./levelCalc-student.service";
import { authenticate } from '../decorators';


@injectable()
export class CatchCritterService {
  constructor(
    @service(CritterStatsService) protected critterStatsService: CritterStatsService,
    @service(LevelCalcStudentService) protected levelCalcStudentService : LevelCalcStudentService,
    @repository(CritterRepository) protected critterRepository: CritterRepository,
    @repository(AttackRepository) protected attackRepository: AttackRepository,
    @repository(CritterAttackRepository) protected critterAttackRepository: CritterAttackRepository,
    @repository(StudentRepository) protected studentRepository: StudentRepository
  ) { }
  @authenticate('jwt')
  async createCopyOfCritter(studentId: number, critterId: number): Promise<CritterUsable> {
    const critter: Critter = await this.critterRepository.findById(critterId, {
      include: ['critterAttacks'],
    });

    // Clone the critter
    const critterCopy: Critter = critter// set id to undefined to create a new entry

    const student: Student = await this.studentRepository.findById(studentId, {
      include: ['critters'],
    })
    const number : number = 0
    const critters: Critter[] = student.critters;
      if(critters.length<200) {
        const savedCritterCopy: Critter = await this.critterRepository.create({
          //critterAttacks:undefined,
          id: undefined,
          studentId: studentId,
          critterTemplateId: critter.critterTemplateId,
          level: critter.level,
          expToNextLevel: critter.expToNextLevel,
          nature: critter.nature,
        });

        //create new critterAttack based on old but with replaced critterId
        await this.critterAttackRepository.createAll(critterCopy.critterAttacks.map(attack => ({
          ...attack, critterId: savedCritterCopy.id,
          id: undefined,
        })));
        await this.levelCalcStudentService.increaseStudentCredits(studentId, 1,25);
        return this.critterStatsService.createCritterUsable(savedCritterCopy.id)
      }else {
        await this.levelCalcStudentService.increaseStudentCredits(studentId, 1,25);

        throw new Error("You reached the max. amount of Critter (max. 200)");
      }
  }
}

