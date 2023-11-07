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
import {StudentHub} from '../models';
import {StudentHubRepository} from '../repositories';

export class StudentHubController {
  constructor(
    @repository(StudentHubRepository)
    public studentHubRepository : StudentHubRepository,
  ) {}

  @post('/student-hubs')
  @response(200, {
    description: 'StudentHub model instance',
    content: {'application/json': {schema: getModelSchemaRef(StudentHub)}},
  })
  async create(
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(StudentHub, {
            title: 'NewStudentHub',
            exclude: ['id'],
          }),
        },
      },
    })
    studentHub: Omit<StudentHub, 'id'>,
  ): Promise<StudentHub> {
    return this.studentHubRepository.create(studentHub);
  }

  @get('/student-hubs/count')
  @response(200, {
    description: 'StudentHub model count',
    content: {'application/json': {schema: CountSchema}},
  })
  async count(
    @param.where(StudentHub) where?: Where<StudentHub>,
  ): Promise<Count> {
    return this.studentHubRepository.count(where);
  }

  @get('/student-hubs')
  @response(200, {
    description: 'Array of StudentHub model instances',
    content: {
      'application/json': {
        schema: {
          type: 'array',
          items: getModelSchemaRef(StudentHub, {includeRelations: true}),
        },
      },
    },
  })
  async find(
    @param.filter(StudentHub) filter?: Filter<StudentHub>,
  ): Promise<StudentHub[]> {
    return this.studentHubRepository.find(filter);
  }

  @patch('/student-hubs')
  @response(200, {
    description: 'StudentHub PATCH success count',
    content: {'application/json': {schema: CountSchema}},
  })
  async updateAll(
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(StudentHub, {partial: true}),
        },
      },
    })
    studentHub: StudentHub,
    @param.where(StudentHub) where?: Where<StudentHub>,
  ): Promise<Count> {
    return this.studentHubRepository.updateAll(studentHub, where);
  }

  @get('/student-hubs/{id}')
  @response(200, {
    description: 'StudentHub model instance',
    content: {
      'application/json': {
        schema: getModelSchemaRef(StudentHub, {includeRelations: true}),
      },
    },
  })
  async findById(
    @param.path.number('id') id: number,
    @param.filter(StudentHub, {exclude: 'where'}) filter?: FilterExcludingWhere<StudentHub>
  ): Promise<StudentHub> {
    return this.studentHubRepository.findById(id, filter);
  }

  @patch('/student-hubs/{id}')
  @response(204, {
    description: 'StudentHub PATCH success',
  })
  async updateById(
    @param.path.number('id') id: number,
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(StudentHub, {partial: true}),
        },
      },
    })
    studentHub: StudentHub,
  ): Promise<void> {
    await this.studentHubRepository.updateById(id, studentHub);
  }

  @put('/student-hubs/{id}')
  @response(204, {
    description: 'StudentHub PUT success',
  })
  async replaceById(
    @param.path.number('id') id: number,
    @requestBody() studentHub: StudentHub,
  ): Promise<void> {
    await this.studentHubRepository.replaceById(id, studentHub);
  }

  @del('/student-hubs/{id}')
  @response(204, {
    description: 'StudentHub DELETE success',
  })
  async deleteById(@param.path.number('id') id: number): Promise<void> {
    await this.studentHubRepository.deleteById(id);
  }
}
