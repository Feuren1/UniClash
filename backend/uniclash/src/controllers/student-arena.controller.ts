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
  Student,
  Arena,
} from '../models';
import {StudentRepository} from '../repositories';
import {authenticate} from "@loopback/authentication";

export class StudentArenaController {
  constructor(
    @repository(StudentRepository) protected studentRepository: StudentRepository,
  ) { }

  @authenticate('jwt')
  @get('/students/{id}/arenas', {
    responses: {
      '200': {
        description: 'Array of Student has many Arena',
        content: {
          'application/json': {
            schema: {type: 'array', items: getModelSchemaRef(Arena)},
          },
        },
      },
    },
  })
  async find(
    @param.path.number('id') id: number,
    @param.query.object('filter') filter?: Filter<Arena>,
  ): Promise<Arena[]> {
    return this.studentRepository.arenas(id).find(filter);
  }

  @authenticate('jwt')
  @post('/students/{id}/arenas', {
    responses: {
      '200': {
        description: 'Student model instance',
        content: {'application/json': {schema: getModelSchemaRef(Arena)}},
      },
    },
  })
  async create(
    @param.path.number('id') id: typeof Student.prototype.id,
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(Arena, {
            title: 'NewArenaInStudent',
            exclude: ['id'],
            optional: ['studentId']
          }),
        },
      },
    }) arena: Omit<Arena, 'id'>,
  ): Promise<Arena> {
    return this.studentRepository.arenas(id).create(arena);
  }

  @authenticate('jwt')
  @patch('/students/{id}/arenas', {
    responses: {
      '200': {
        description: 'Student.Arena PATCH success count',
        content: {'application/json': {schema: CountSchema}},
      },
    },
  })
  async patch(
    @param.path.number('id') id: number,
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(Arena, {partial: true}),
        },
      },
    })
    arena: Partial<Arena>,
    @param.query.object('where', getWhereSchemaFor(Arena)) where?: Where<Arena>,
  ): Promise<Count> {
    return this.studentRepository.arenas(id).patch(arena, where);
  }

  @authenticate('jwt')
  @del('/students/{id}/arenas', {
    responses: {
      '200': {
        description: 'Student.Arena DELETE success count',
        content: {'application/json': {schema: CountSchema}},
      },
    },
  })
  async delete(
    @param.path.number('id') id: number,
    @param.query.object('where', getWhereSchemaFor(Arena)) where?: Where<Arena>,
  ): Promise<Count> {
    return this.studentRepository.arenas(id).delete(where);
  }
}
