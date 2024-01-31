import {
  Count,
  CountSchema,
  Filter,
  FilterExcludingWhere,
  repository,
  Where,
} from '@loopback/repository';
import {
  post,
  param,
  get,
  getModelSchemaRef,
  patch,
  put,
  del,
  requestBody,
  response,
} from '@loopback/rest';
import {CritterInFight} from '../models';
import {CritterInFightRepository, OnlineFightRepository} from '../repositories';
import {service} from "@loopback/core";
import {OnlineFightService} from "../services/online-fight.service";
import {FightInformation} from "../models/fight-information.model";
import {CritterInFightInformation} from "../models/critter-in-fight-information.model";

export class LiveFightController {
  constructor(
    @repository(CritterInFightRepository)
    public critterInFightRepository : CritterInFightRepository,
    @repository(OnlineFightRepository)
    public onlineFightRepository : OnlineFightRepository,
    @service(OnlineFightService)
    public onlineFightService : OnlineFightService,
  ) {}

  @put('/createFight/{studentId}/{enemyStudentId}')
  @response(200, {
    description: 'Create an Online fight against a student',
    content: {'application/json': {schema: getModelSchemaRef(CritterInFight)}},
  })
  async createFight(
      @param.path.number('studentId') studentId: number,
      @param.path.number('enemyStudentId') enemyStudentId: number,
  ): Promise<void> {
    await this.onlineFightService.createFight(studentId, enemyStudentId);
  }

  @put('/makingDamage/{fightConnectionId}/{studentId}/{amountOfDamage}')
  @response(200, {
    description: 'execute damage',
    content: {'application/json': {schema: getModelSchemaRef(CritterInFight)}},
  })
  async makingDamage(
      @param.path.number('fightConnectionId') fightConnectionId: number,
      @param.path.number('studentId') studentId: number,
      @param.path.number('amountOfDamage') amountOfDamage: number,
  ): Promise<void> {
    await this.onlineFightService.makingDamage(fightConnectionId,studentId, amountOfDamage);
  }

  @put('/insertCritter/{fightConnectionId}/{studentId}/{critterId}')
  @response(200, {
    description: 'insert a critter for fighting',
    content: {'application/json': {schema: getModelSchemaRef(CritterInFight)}},
  })
  async insertCritter(
      @param.path.number('fightConnectionId') fightConnectionId: number,
      @param.path.number('studentId') studentId: number,
      @param.path.number('critterId') critterId: number,
  ): Promise<void> {
    await this.onlineFightService.insertCritter(critterId,studentId, fightConnectionId);
  }

  @put('/checkIfFightCanStart/{fightConnectionId}')
  @response(200, {
    description: 'Checks if both students are ready to fight',
    content: {'application/json': {schema: getModelSchemaRef(CritterInFight)}},
  })
  async checkIfFightCanStart(
      @param.path.number('fightConnectionId') fightConnectionId: number,
  ): Promise<void> {
    await this.onlineFightService.checkIfFightCanStart(fightConnectionId);
  }

  @get('/checkMyState/{fightConnectionId}/{studentId}')
  @response(200, {
    description: 'Checks the current State of the fight',
    content: {'application/json': {schema: CountSchema}},
  })
  async checkMyState(
      @param.path.number('fightConnectionId') fightConnectionId: number,
      @param.path.number('studentId') studentId: number,
  ):  Promise<{ state: string }> {
    const state = await this.onlineFightService.checkMyState(fightConnectionId, studentId);
    return { state };
  }

  @get('/fightInformationList/{studentId}')
  @response(200, {
    description: 'Readable information of a fight',
    content: {'application/json': {schema: CountSchema}},
  })
  async fightInformationList(
      @param.path.number('studentId') studentId: number,
  ): Promise<FightInformation[]> {
    return this.onlineFightService.fightInformationList(studentId);
  }

  @get('/getCritterInformation/{critterId}')
  @response(200, {
    description: 'Readable information of a fight',
    content: {'application/json': {schema: CountSchema}},
  })
  async getCritterInformation(
      @param.path.number('critterId') critterId: number,
  ): Promise<CritterInFightInformation> {
    return this.onlineFightService.getCritterInformation(critterId);
  }

  @get('/getCritterInformationFromEnemy/{fightConnectionId}/{studentId}')
  @response(200, {
    description: 'Readable information of a fight',
    content: {'application/json': {schema: CountSchema}},
  })
  async getCritterInformationFromEnemy(
      @param.path.number('fightConnectionId') fightConnectionId: number,
      @param.path.number('studentId') studentId: number,
  ): Promise<CritterInFightInformation> {
    return this.onlineFightService.getCritterInformationFromEnemy(fightConnectionId,studentId);
  }
}
