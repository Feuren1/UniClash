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
import {StudentCritterService} from '../services/student-critter.service';

export class StudentCritterController {
  constructor(
    @service(StudentCritterService) protected studentCritterService: StudentCritterService,
    @repository(StudentRepository) protected studentRepository: StudentRepository,
  ) { }

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
    return this.studentCritterService.createCritterUsableListOnStudentId(id);
  }

  @get('/usables', {
    responses: {
      '200': {
        description: 'Calculate and return CritterUsable of all critters',
        content: {
          'application/json': {
            schema: getModelSchemaRef(CritterUsable), // Use CritterUsable schema
          },
        },
      },
    },
  })
  async calculateAllCritterUsable(
    @param.path.number('id') id: number,
  ): Promise<CritterUsable[]> {
    return this.studentCritterService.createCritterUsableListOfAll();
  }
}
