import {
  repository,
} from '@loopback/repository';
import {
  param,
  get,
  getModelSchemaRef,
} from '@loopback/rest';
import {
  Arena,
  Student,
} from '../models';
import {ArenaRepository} from '../repositories';
import {authenticate} from "@loopback/authentication";

export class ArenaStudentController {
  constructor(
    @repository(ArenaRepository)
    public arenaRepository: ArenaRepository,
  ) { }

  @authenticate('jwt')
  @get('/arenas/{id}/student', {
    responses: {
      '200': {
        description: 'Student belonging to Arena',
        content: {
          'application/json': {
            schema: getModelSchemaRef(Student),
          },
        },
      },
    },
  })
  async getStudent(
    @param.path.number('id') id: typeof Arena.prototype.id,
  ): Promise<Student> {
    return this.arenaRepository.student(id);
  }
}
