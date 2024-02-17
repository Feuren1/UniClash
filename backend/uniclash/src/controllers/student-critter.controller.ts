import {service} from '@loopback/core';
import {
  Count,
  CountSchema,
  Filter,
  repository,
  Where,
} from '@loopback/repository';
import {
  del,
  get,
  getModelSchemaRef,
  getWhereSchemaFor,
  param,
  patch,
  post,
  requestBody,
} from '@loopback/rest';
import {
  Critter,
  CritterUsable,
  Student,
} from '../models';
import {StudentRepository} from '../repositories';
import {CatchCritterService} from "../services/catch-critter.service";
import {StudentCritterService} from '../services/student-critter.service';
import {authenticate} from "@loopback/authentication";
import {CritterListable} from "../models/critter-listable.model";
import {WildEncounterInformationService} from "../services/wildEncounterInformation.service";

export class StudentCritterController {
  constructor(
    @service(StudentCritterService) protected studentCritterService: StudentCritterService,
    @repository(StudentRepository) protected studentRepository: StudentRepository,
    @service(CatchCritterService) protected catchCritterService: CatchCritterService,
    @service(WildEncounterInformationService) protected wildEncounterInformationService: WildEncounterInformationService,
  ) { }

  @authenticate('jwt')
  @get('/students/{id}/critters', {
    responses: {
      '200': {
        description: 'Array of Student has many Critter',
        content: {
          'application/json': {
            schema: {type: 'array', items: getModelSchemaRef(Critter)},
          },
        },
      },
    },
  })
  async find(
    @param.path.number('id') id: number,
    @param.query.object('filter') filter?: Filter<Critter>,
  ): Promise<Critter[]> {
    return this.studentRepository.critters(id).find(filter);
  }

  @authenticate('jwt')
  @post('/students/{id}/critters', {
    responses: {
      '200': {
        description: 'Student model instance',
        content: {'application/json': {schema: getModelSchemaRef(Critter)}},
      },
    },
  })
  async create(
    @param.path.number('id') id: typeof Student.prototype.id,
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(Critter, {
            title: 'NewCritterInStudent',
            exclude: ['id'],
            optional: ['studentId']
          }),
        },
      },
    }) critter: Omit<Critter, 'id'>,
  ): Promise<Critter> {

    return this.studentRepository.critters(id).create(critter);
  }

  @authenticate('jwt')
  @patch('/students/{id}/critters', {
    responses: {
      '200': {
        description: 'Student.Critter PATCH success count',
        content: {'application/json': {schema: CountSchema}},
      },
    },
  })
  async patch(
    @param.path.number('id') id: number,
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(Critter, {partial: true}),
        },
      },
    })
    critter: Partial<Critter>,
    @param.query.object('where', getWhereSchemaFor(Critter)) where?: Where<Critter>,
  ): Promise<Count> {
    return this.studentRepository.critters(id).patch(critter, where);
  }

  @authenticate('jwt')
  @del('/students/{id}/critters', {
    responses: {
      '200': {
        description: 'Student.Critter DELETE success count',
        content: {'application/json': {schema: CountSchema}},
      },
    },
  })
  async delete(
    @param.path.number('id') id: number,
    @param.query.object('where', getWhereSchemaFor(Critter)) where?: Where<Critter>,
  ): Promise<Count> {
    return this.studentRepository.critters(id).delete(where);
  }

  @authenticate('jwt')
  @get('/students/{id}/usables', {
    responses: {
      '200': {
        description: 'Calculate and return CritterUsable for all critters of a student',
        content: {
          'application/json': {
            schema: getModelSchemaRef(CritterUsable), // Use CritterUsable schema
          },
        },
      },
    },
  })
  async calculateCritterUsable(
    @param.path.number('id') id: number,
  ): Promise<CritterUsable[]> {
    await this.wildEncounterInformationService.checkOfWildEncounterShouldReload()
    return this.studentCritterService.createCritterUsableListOnStudentId(id);
  }

  @authenticate('jwt')
  @get('/students/{id}/listables', {
    responses: {
      '200': {
        description: 'Returns CritterListables for all critters of a student',
        content: {
          'application/json': {
            schema: getModelSchemaRef(CritterUsable), // Use CritterUsable schema
          },
        },
      },
    },
  })
  async calculateCritterListables(
      @param.path.number('id') id: number,
  ): Promise<CritterListable[]> {
    await this.wildEncounterInformationService.checkOfWildEncounterShouldReload()
    return this.studentCritterService.createCritterListableListOnStudentId(id);
  }
  @authenticate('jwt')
  @post('/students/{studentId}/critters/{critterId}/catchCritter', {
    responses: {
      '200': {
        description: 'Adds a newly caught critter to the student',
        content: {'application/json': {schema: getModelSchemaRef(Critter)}},
      },
    },
  })
  async catchCritter(
    @param.path.number('studentId') studentId: typeof Student.prototype.id,
    @param.path.number('critterId') critterId: typeof Critter.prototype.id,
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(Critter, {
            title: 'NewCritterInStudent',
            exclude: ['id'],
            optional: ['studentId']
          }),
        },
      },
    }) critter: Omit<Critter, 'id'>,
  ): Promise<CritterUsable> {
    return this.catchCritterService.createCopyOfCritter(studentId,
      critterId);
  }
}
