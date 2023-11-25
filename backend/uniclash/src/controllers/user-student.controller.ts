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
  User,
  Student,
} from '../models';
import {UserRepository} from '../repositories';

export class UserStudentController {
  constructor(
    @repository(UserRepository) protected userRepository: UserRepository,
  ) { }

  @get('/users/{id}/student', {
    responses: {
      '200': {
        description: 'User has one Student',
        content: {
          'application/json': {
            schema: getModelSchemaRef(Student),
          },
        },
      },
    },
  })
  async get(
    @param.path.string('id') id: string,
    @param.query.object('filter') filter?: Filter<Student>,
  ): Promise<Student> {
    return this.userRepository.student(id).get(filter);
  }

  @post('/users/{id}/student', {
    responses: {
      '200': {
        description: 'User model instance',
        content: {'application/json': {schema: getModelSchemaRef(Student)}},
      },
    },
  })
  async create(
    @param.path.string('id') id: typeof User.prototype.id,
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(Student, {
            title: 'NewStudentInUser',
            exclude: ['id'],
            optional: ['userId']
          }),
        },
      },
    }) student: Omit<Student, 'id'>,
  ): Promise<Student> {
    return this.userRepository.student(id).create(student);
  }

  @patch('/users/{id}/student', {
    responses: {
      '200': {
        description: 'User.Student PATCH success count',
        content: {'application/json': {schema: CountSchema}},
      },
    },
  })
  async patch(
    @param.path.string('id') id: string,
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(Student, {partial: true}),
        },
      },
    })
    student: Partial<Student>,
    @param.query.object('where', getWhereSchemaFor(Student)) where?: Where<Student>,
  ): Promise<Count> {
    return this.userRepository.student(id).patch(student, where);
  }

  @del('/users/{id}/student', {
    responses: {
      '200': {
        description: 'User.Student DELETE success count',
        content: {'application/json': {schema: CountSchema}},
      },
    },
  })
  async delete(
    @param.path.string('id') id: string,
    @param.query.object('where', getWhereSchemaFor(Student)) where?: Where<Student>,
  ): Promise<Count> {
    return this.userRepository.student(id).delete(where);
  }
}
