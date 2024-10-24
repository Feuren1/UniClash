import {
  repository,
} from '@loopback/repository';
import {
  param,
  get,
  getModelSchemaRef,
} from '@loopback/rest';
import {
  Student,
  User,
} from '../models';
import {StudentRepository} from '../repositories';
import {authenticate} from "@loopback/authentication";

export class StudentUserController {
  constructor(
    @repository(StudentRepository)
    public studentRepository: StudentRepository,
  ) { }

  @authenticate('jwt')
  @get('/students/{id}/user', {
    responses: {
      '200': {
        description: 'User belonging to Student',
        content: {
          'application/json': {
            schema: getModelSchemaRef(User),
          },
        },
      },
    },
  })
  async getUser(
    @param.path.number('id') id: typeof Student.prototype.id,
  ): Promise<User> {
    return this.studentRepository.user(id);
  }
}
