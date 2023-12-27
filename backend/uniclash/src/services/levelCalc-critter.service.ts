import {injectable, service} from '@loopback/core';
import {repository} from "@loopback/repository";
import {Critter, CritterRelations, CritterTemplate, Student} from "../models";
import {CritterRepository, CritterTemplateRepository, StudentRepository} from "../repositories";

@injectable()
export class LevelCalcCritterService {
  constructor(
    @repository(CritterTemplateRepository) protected critterTemplateRepository: CritterTemplateRepository,
    @repository(CritterRepository) protected critterRepository: CritterRepository
  ) { }

  async checkForLevelUp(critterId: number): Promise<void> {
    const critter: Critter = await this.critterRepository.findById(critterId);
    if (!critter) {
      throw new Error(`Student with ID ${critter} not found. CheckForLevel Up Methode`);
    };

    if(critter.level == null){
      critter.level = 0;
    }

    const neededExp : number = Math.floor(50 * Math.pow((1.025), critter.level + 1))

    if(critter.expToNextLevel != null){
      while(critter.expToNextLevel >= neededExp){
        critter.expToNextLevel = critter.expToNextLevel - neededExp;
        critter.level++;
      }
    }

    await this.critterRepository.update(critter);
  }

  async increaseCritterExp(critterId: number,expToAdd : number): Promise<void> {
    const critter: Critter = await this.critterRepository.findById(critterId);
    if (!critter) {
      throw new Error(`Student with ID ${critter} not found. increase StudentCredits`);
    };
    critter.expToNextLevel = (critter.expToNextLevel || 0) + expToAdd;

    await this.critterRepository.update(critter);
    await this.checkForLevelUp(critterId);
  }

  async evolveCritter(critterId : number): Promise<Critter>{
    const critter: Critter = await this.critterRepository.findById(critterId);
    const critterTemplate: CritterTemplate = await this.critterTemplateRepository.findById(critter.critterTemplateId);
    if(critterTemplate.evolvesIntoTemplateId != null && critterTemplate.evolvesIntoTemplateId != 0){
      if(critter.level >= critterTemplate.evolesAt){
        critter.critterTemplateId = critterTemplate.evolvesIntoTemplateId
        await this.critterRepository.update(critter);
      }
    }
    return critter
  }
}

