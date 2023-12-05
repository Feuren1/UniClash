import {injectable, service} from '@loopback/core';
import {repository} from "@loopback/repository";
import {Critter, CritterUsable, Student} from "../models";
import {AttackRepository, CritterAttackRepository, CritterRepository, StudentRepository} from "../repositories";
import {CritterStatsService} from "./critter-stats.service";


@injectable()
export class CatchCritterService {
  constructor(
    @service(CritterStatsService) protected critterStatsService: CritterStatsService,
    @repository(CritterRepository) protected critterRepository: CritterRepository,
    @repository(AttackRepository) protected attackRepository: AttackRepository,
    @repository(CritterAttackRepository) protected critterAttackRepository: CritterAttackRepository,
    @repository(StudentRepository) protected studentRepository: StudentRepository
  ) { }

  async createCopyOfCritter(studentId: number, critterId: number): Promise<CritterUsable> {
    const critter: Critter = await this.critterRepository.findById(critterId, {
      include: ['critterAttacks'],
    });

    // Clone the critter
    const critterCopy: Critter = critter// set id to undefined to create a new entry
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

    await this.increaseStudentCredits(studentId, 10);

    return this.critterStatsService.createCritterUsable(savedCritterCopy.id)
  }

  async increaseStudentCredits(studentId: number, creditsToAdd: number): Promise<void> {
    const student: Student = await this.studentRepository.findById(studentId);
    if (!student) {
      throw new Error(`Student with ID ${studentId} not found.`);
    };
    student.credits = (student.credits || 0) + creditsToAdd;

    await this.studentRepository.update(student);
  }
}

