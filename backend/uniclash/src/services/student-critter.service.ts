import {inject, injectable} from '@loopback/core';
import {repository} from '@loopback/repository';
import {Critter, CritterUsable, Student} from '../models';
import {AttackRepository, CritterAttackRepository, CritterRepository, CritterTemplateRepository, StudentRepository} from '../repositories';
import {CritterStatsService} from './critter-stats.service';
import {CritterListable} from "../models/critter-listable.model";

@injectable()
export class StudentCritterService {
  constructor(
    @repository(CritterTemplateRepository) protected critterTemplateRepository: CritterTemplateRepository,
    @repository(CritterRepository) protected critterRepository: CritterRepository,
    @repository(CritterAttackRepository) protected critterAttackRepository: CritterAttackRepository,
    @repository(AttackRepository) protected attackRepository: AttackRepository,
    @repository(StudentRepository) protected studentRepository: StudentRepository,
    @inject('services.CritterStatsService') // Inject the CritterStatsService
    protected critterStatsService: CritterStatsService,
  ) { }

  async createCritterUsableListOnStudentId(studentId: number): Promise<CritterUsable[]> {
    const student: Student = await this.studentRepository.findById(studentId, {
      include: ['critters'],
    })

    const critters: Critter[] = student.critters;
    const critterUsables: CritterUsable[] = [];

    for (const critter of critters) {

      //if (critter.id !== undefined) {//other option is to remove the ? in the model at id? was used
      //}
      const critterUsable = await this.critterStatsService.createCritterUsable(critter.id);
      critterUsables.push(critterUsable);

    }

    return critterUsables;
  }

  async createCritterListableListOnStudentId(studentId: number): Promise<CritterListable[]> {
    const student: Student = await this.studentRepository.findById(studentId, {
      include: ['critters'],
    })
    const critterTemplates = await  this.critterTemplateRepository.find();

    const critters  = student.critters;
    const critterListables: CritterListable[] = [];

    for (const critterS of critters) {
      let name = ""
      for(const template of critterTemplates){
        if(template.id == critterS.critterTemplateId && template.name != null){
          name = template.name
              break;
        }
      }
      const critterListable = new CritterListable({
        level: critterS.level,
        critterId: critterS.id,
        name: name,
          });

      critterListables.push(critterListable);

    }

    return critterListables;
  }

  async createCritterUsableListOfAll(): Promise<CritterUsable[]> {

    const critters: Critter[] = await this.critterRepository.find();
    const critterUsables: CritterUsable[] = [];

    for (const critter of critters) {

      //if (critter.id !== undefined) {//other option, that was used, is to remove the ? in the model at id?
      //}
      const critterUsable = await this.critterStatsService.createCritterUsable(critter.id);
      critterUsables.push(critterUsable);

    }

    return critterUsables;
  }
}
