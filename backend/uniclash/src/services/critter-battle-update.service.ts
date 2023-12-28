import {injectable, service} from '@loopback/core';
import {repository} from "@loopback/repository";
import {Attack, Critter, CritterUsable, Student} from "../models";
import {AttackRepository, CritterAttackRepository, CritterRepository, StudentRepository} from "../repositories";
import {CritterStatsService} from "./critter-stats.service";
import {LevelCalcStudentService} from "./levelCalc-student.service";
import { authenticate } from '../decorators';


@injectable()
export class CritterBattleUpdateService {
  constructor(
    @service(CritterStatsService) protected critterStatsService: CritterStatsService,
    @service(LevelCalcStudentService) protected levelCalcStudentService : LevelCalcStudentService,
    @repository(CritterRepository) protected critterRepository: CritterRepository,
    @repository(AttackRepository) protected attackRepository: AttackRepository,
    @repository(CritterAttackRepository) protected critterAttackRepository: CritterAttackRepository,
    @repository(StudentRepository) protected studentRepository: StudentRepository
  ) { }

  @authenticate('jwt')
  async increaseCritterXp(studentId: number, critterId: number, xpIncrease: number): Promise<Critter> {
    // Find the critter by ID and include related data
    const critter: Critter = await this.critterRepository.findById(critterId, {
      include: ['critterAttacks'],
    });
  
    // Check if the critter exists
    if (!critter) {
      throw new Error(`Critter with ID ${critterId} not found`);
    }
    const newExpToNextLevel = (critter.expToNextLevel || 0) + xpIncrease;
    critter.expToNextLevel = newExpToNextLevel;
    if (newExpToNextLevel >= 100) {
      critter.level += 1;
      // Keep the remaining XP after leveling up
      critter.expToNextLevel = newExpToNextLevel % 100;
    }
    // Save the updated critter to the database
    await this.critterRepository.update(critter);
    await this.levelCalcStudentService.increaseStudentCredits(studentId, 1,25);
    return critter; // Return the updated critter
  }

}
