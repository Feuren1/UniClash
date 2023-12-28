import {
  repository,
} from '@loopback/repository';
import {
  param,
  get,
  getModelSchemaRef,
} from '@loopback/rest';
import {
  Critter,
  Student,
} from '../models';
import {CritterRepository} from '../repositories';
import {authenticate} from "@loopback/authentication";

export class CritterStudentController {
  constructor(
    @repository(CritterRepository)
    public critterRepository: CritterRepository,
  ) { }

  @authenticate('jwt')
  @get('/critters/{id}/student', {
    responses: {
      '200': {
        description: 'Student belonging to Critter',
        content: {
          'application/json': {
            schema: getModelSchemaRef(Student),
          },
        },
      },
    },
  })
  async getStudent(
    @param.path.number('id') id: typeof Critter.prototype.id,
  ): Promise<Student> {
    return this.critterRepository.student(id);
  }
}
