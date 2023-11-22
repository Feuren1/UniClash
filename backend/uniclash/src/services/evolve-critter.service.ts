import {injectable} from '@loopback/core';
import {repository} from '@loopback/repository';
import {Critter, CritterTemplate} from '../models';
import {AttackRepository, CritterAttackRepository, CritterRepository, CritterTemplateRepository, StudentRepository} from '../repositories';

@injectable()
export class EvolveCritterService {
  constructor(
    @repository(CritterTemplateRepository) protected critterTemplateRepository: CritterTemplateRepository,
    @repository(CritterRepository) protected critterRepository: CritterRepository,
    @repository(CritterAttackRepository) protected critterAttackRepository: CritterAttackRepository,
    @repository(AttackRepository) protected attackRepository: AttackRepository,
    @repository(StudentRepository) protected studentRepository: StudentRepository,
    //@inject('services.CritterStatsService') // Inject the CritterStatsService
    protected evolveCritterService: EvolveCritterService,
  ) { }

  async evolveCritter(critterId: number): Promise<Critter> {
    const critterToReplace: Critter = await this.critterRepository.findById(critterId, {
      include: ['critterTemplate', 'student', 'critterAttacks'],
    })
    const critterTemplateFromToReplace: CritterTemplate = await this.critterTemplateRepository.findById(critterId, {
      include: ['evolvesInto'],
    })
    const critterTemplateToCreateNew: CritterTemplate = await this.critterTemplateRepository.findById(critterTemplateFromToReplace.evolvesIntoTemplateId)

    const critterEvolved: Critter = await this.critterRepository.create({
      level: critterToReplace.level,
      critterTemplateId: critterTemplateToCreateNew.id,
      studentId: critterToReplace.studentId
    })

    //create new critterAttack based on old but with replaced critterId
    await this.critterAttackRepository.createAll(critterToReplace.critterAttacks.map(attack => ({...attack, critterId: critterEvolved.id})));


    await this.critterRepository.deleteById(critterId);
    await this.critterAttackRepository.deleteAll({critterId: critterId});

    return critterEvolved
  }


}
